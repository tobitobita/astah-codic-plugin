package dsk.codic.model.impl;

import dsk.codic.model.EngineResult;
import java.io.StringReader;
import javax.json.Json;
import javax.json.stream.JsonParser;
import lombok.Data;
import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * V1 API を使用した時のエンジン結果。
 */
@Data
public class EngineResultV1 implements EngineResult {

    /**
     * （翻訳する）文字列。
     */
    private String text;
    /**
     * 翻訳した文字列。
     */
    private String translatedText;
    /**
     * 成功可否。
     */
    private boolean success;

    /**
     * V1 APIを使用した時のレスポンスJsonをパースする。
     *
     * @param rawJson 生のjsonレスポンス文字列。
     * @return V1用のエンジン結果。
     */
    public static EngineResultV1 parseEngineJsonV1(final String rawJson) {
        final JsonParser parser = Json.createParser(new StringReader(rawJson));
        int arrayCount = -1;
        int objectCount = -1;
        final EngineResultV1 result = new EngineResultV1();
        String key = null;
        while (parser.hasNext()) {
            // ピンポイントで必要なものを取得したいため、下記判定を行っている。
            if (arrayCount > 0 || objectCount > 0) {
                break;
            }
            final JsonParser.Event event = parser.next();
            switch (event) {
                case KEY_NAME:
                    key = parser.getString();
                    System.out.printf("keyName:%s\n", key);
                    break;
                case VALUE_STRING:
                    final String value = parser.getString();
                    switch (defaultString(key)) {
                        case "text":
                            result.setText(value);
                            break;
                        case "translated_text":
                            result.setTranslatedText(value);
                            break;
                        default:
                            break;
                    }
                    break;
                case VALUE_NUMBER:
                    System.out.printf("valueNumber:%s\n", parser.getBigDecimal().toString());
                    break;
                case VALUE_TRUE:
                    switch (defaultString(key)) {
                        case "successful":
                            result.setSuccess(true);
                            break;
                        default:
                            break;
                    }
                    break;
                case VALUE_FALSE:
                    switch (defaultString(key)) {
                        case "successful":
                            result.setSuccess(false);
                            break;
                        default:
                            break;
                    }
                    break;
                case VALUE_NULL:
                    System.out.printf("valueNull\n");
                    break;
                case START_OBJECT:
                    ++objectCount;
                    break;
                case END_OBJECT:
                    break;
                case START_ARRAY:
                    ++arrayCount;
                    break;
                case END_ARRAY:
                    break;
                default:
                    break;
            }
        }
        return result;
    }
}
