-- Lua скрипт для атомарного поиска и удаления пары игроков из Redis Sorted Set
-- Входные параметры: ключ пула, ID игрока, рейтинг, дельта
local pool_key = KEYS[1]
local user_id = ARGV[1]
local rating = tonumber(ARGV[2])
local delta = tonumber(ARGV[3])

-- 1. Добавляем текущего игрока в пул (если его там еще нет)
-- Используем NX чтобы не перезаписывать существующий рейтинг
redis.call('ZADD', pool_key, 'NX', rating, user_id)

-- 2. Ищем подходящего оппонента в диапазоне [rating - delta, rating + delta]
-- Получаем всех игроков в диапазоне (может включать самого игрока)
local opponents = redis.call('ZRANGEBYSCORE', pool_key, rating - delta, rating + delta, 'LIMIT', 0, 10)

-- 3. Находим первого оппонента, который не является текущим игроком
-- Важно: исключаем только самого себя, чтобы не создавать пару с самим собой
local opponent_id = nil
for i = 1, #opponents do
    if opponents[i] ~= user_id then
        opponent_id = opponents[i]
        break
    end
end

if opponent_id then
    -- 4. Атомарно удаляем обоих из пула
    -- Проверяем, что оба игрока еще в пуле (защита от race condition, когда оба скрипта выполняются одновременно)
    local user_exists = redis.call('ZSCORE', pool_key, user_id)
    local opponent_exists = redis.call('ZSCORE', pool_key, opponent_id)
    
    if user_exists and opponent_exists then
        -- Удаляем обоих атомарно
        redis.call('ZREM', pool_key, user_id)
        redis.call('ZREM', pool_key, opponent_id)
        
        -- 5. Возвращаем пару [player1, player2]
        return {user_id, opponent_id}
    end
end

-- Если пара не найдена, возвращаем nil (игрок остается в пуле)
return nil
