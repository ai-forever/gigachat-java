package chat.giga.model;

public class ModelName {

    /**
     * Модели первого поколения недоступны. Все запросы к этим моделям автоматически перенаправляются на их аналоги
     * второго поколения
     */
    @Deprecated
    public static final String GIGA_CHAT = "GigaChat";

    @Deprecated
    public static final String GIGA_CHAT_PRO = "GigaChat-Pro";

    @Deprecated
    public static final String GIGA_CHAT_MAX = "GigaChat-Max";

    /**
     * Подойдет для быстрого решения рутинных задач. Модель не требовательна к железу и доступна по цене
     */
    public static final String GIGA_CHAT_2 = "GigaChat-2";

    /**
     *  Модель лучше справляется со сложными инструкциями и решает комплексные задачи. Сильна в суммаризации, рерайтинге, редактировании текстов и ответах на вопросы.
     *  Эффективна в прикладных областях, например, в экономике и юриспруденции
     */
    public static final String GIGA_CHAT_PRO_2 = "GigaChat-2-Pro";
    /**
     *  Показывает лучшие результаты в области биологии, химии и физики. Запоминает контекст в многошаговых диалогах, работает с длинными текстами на русском языке и отвечает быстрее
     */
    public static final String GIGA_CHAT_MAX_2 = "GigaChat-2-Max";

    public static final String GIGA_CHAT_ULTRA_3 = "GigaChat-3-Ultra";

    /**
     * Базовая модель, доступная по умолчанию для векторного представления текстов
     */
    public static final String EMBEDDINGS = "Embeddings";

    /**
     * Доработанная и улучшенная версия базовой модели Embeddings
     */
    public static final String EMBEDDINGS_2 = "Embeddings-2";

    /**
     * Продвинутая модель с большим размером контекста
     */
    public static final String EMBEDDINGS_GIGA_R = "EmbeddingsGigaR";

    /**
     * Развитая модель для создания векторного представления текста
     */
    public static final String EMBEDDINGS_GIGA_3B = "GigaEmbeddings-3B-2025-09";
}
