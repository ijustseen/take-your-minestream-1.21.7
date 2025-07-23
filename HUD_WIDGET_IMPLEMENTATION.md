# Реализация HUD виджета для отображения сообщений

## Обзор изменений

Была реализована новая функциональность для отображения сообщений Twitch-чата в виде HUD виджета в правом верхнем углу экрана, вместо 3D объектов в игровом мире.

## Основные изменения

### 1. Создан enum MessageSpawnMode

- **Файл**: `src/client/java/takeyourminestream/modid/config/MessageSpawnMode.java`
- **Режимы**:
  - `AROUND_PLAYER` - сообщения появляются вокруг игрока (старое поведение)
  - `FRONT_OF_PLAYER` - сообщения появляются только перед игроком (старое поведение)
  - `HUD_WIDGET` - сообщения отображаются как HUD виджет в правом верхнем углу

### 2. Обновлена модель конфигурации

- **Файл**: `src/client/java/takeyourminestream/modid/config/ModConfigData.java`
- Заменено поле `boolean messagesInFrontOfPlayerOnly` на `MessageSpawnMode messageSpawnMode`
- Добавлены методы для обратной совместимости

### 3. Обновлен ConfigManager

- **Файл**: `src/client/java/takeyourminestream/modid/ConfigManager.java`
- Добавлена поддержка нового поля `messageSpawnMode` в кэше конфигурации
- Добавлена обработка нового поля в методе `setConfigValue`

### 4. Обновлен ModConfig

- **Файл**: `src/client/java/takeyourminestream/modid/ModConfig.java`
- Добавлены методы `getMESSAGE_SPAWN_MODE()` и `setMESSAGE_SPAWN_MODE()`

### 5. Создан MessageHudRenderer

- **Файл**: `src/client/java/takeyourminestream/modid/messages/MessageHudRenderer.java`
- Отвечает за отображение сообщений как HUD виджет
- **Особенности**:
  - Отображает до 5 последних сообщений
  - Сообщения появляются в правом верхнем углу экрана
  - Поддерживает перенос текста для длинных сообщений
  - Плавное исчезновение сообщений с альфа-каналом
  - Полупрозрачный черный фон для лучшей читаемости

### 6. Обновлен MessageSystemFactory

- **Файл**: `src/client/java/takeyourminestream/modid/messages/MessageSystemFactory.java`
- Добавлена инициализация `MessageHudRenderer`

### 7. Обновлен MessageSpawner

- **Файл**: `src/client/java/takeyourminestream/modid/messages/MessageSpawner.java`
- Добавлена логика для обработки HUD режима
- В HUD режиме сообщения создаются с нулевой позицией

### 8. Обновлен MessageRenderer

- **Файл**: `src/client/java/takeyourminestream/modid/messages/MessageRenderer.java`
- Добавлена проверка режима отображения
- В HUD режиме 3D рендеринг сообщений отключается

### 9. Обновлен экран конфигурации

- **Файл**: `src/client/java/takeyourminestream/modid/ModConfigScreen.java`
- Кнопка переключения режима теперь циклически переключает между тремя режимами
- Добавлен метод `getSpawnModeButtonText()` для отображения текущего режима

### 10. Обновлены файлы локализации

- **Файлы**:
  - `src/main/resources/assets/take-your-minestream/lang/ru_ru.json`
  - `src/main/resources/assets/take-your-minestream/lang/en_us.json`
- Добавлен перевод для нового режима `"hud_widget"`

## Как использовать

1. Запустите игру с модом
2. Откройте настройки мода (по умолчанию клавиша `O`)
3. Нажимайте кнопку "Режим спавна сообщений" для переключения между режимами:
   - "Вокруг игрока" / "Around player"
   - "Только FOP" / "FOP only"
   - "HUD виджет" / "HUD widget"
4. Сохраните настройки

## Технические детали

### HUD виджет

- Максимальное количество отображаемых сообщений: 5
- Максимальная ширина сообщения: 300 пикселей
- Отступы: 10 пикселей от края экрана, 6 пикселей внутри панели
- Цвет фона: полупрозрачный черный (0x80000000)
- Цвет текста: белый (0xFFFFFFFF)
- Поддержка переноса текста для длинных сообщений
- Плавное исчезновение в соответствии с настройками времени жизни сообщений

### Обратная совместимость

- Старые конфигурационные файлы автоматически мигрируются
- Методы для работы с `messagesInFrontOfPlayerOnly` сохранены для совместимости
- При загрузке старой конфигурации значение `true` преобразуется в `FRONT_OF_PLAYER`, `false` - в `AROUND_PLAYER`

## Тестирование

Мод был успешно протестирован:

- ✅ Сборка проекта проходит без ошибок
- ✅ Мод запускается и инициализируется корректно
- ✅ Подключение к Twitch-чату работает
- ✅ Конфигурация сохраняется и загружается правильно
- ✅ Новое поле `messageSpawnMode` корректно сериализуется в JSON
