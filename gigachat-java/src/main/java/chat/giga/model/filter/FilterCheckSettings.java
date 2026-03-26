package chat.giga.model.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class FilterCheckSettings implements Serializable {

    /**
     * Версия класса для сериализации. Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Использовать нейросетевую проверку.
     */
    @JsonProperty
    Boolean neuro;

    /**
     * Использовать черный список слов.
     */
    @JsonProperty
    Boolean blacklist;

    /**
     * Использовать белый список слов.
     */
    @JsonProperty
    Boolean whitelist;

    /**
     * Выключение нормализации содержимого.
     * <p>Нормализация, которая применяется: <br>
     * - заменяет role system на user, если после system нет user (System -> User) <br> - конкатенирует сообщения User и
     * Assistant (System User User Assistant Assistant -> System User Assistant)
     */
    @JsonProperty("unnormalized_history")
    Boolean unnormalizedHistory;
}
