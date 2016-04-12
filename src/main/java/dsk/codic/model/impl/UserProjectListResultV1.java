package dsk.codic.model.impl;

import dsk.codic.model.UserProject;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.stream.JsonParser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import dsk.codic.model.UserProjectListResult;
import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * V1 API を使用した時のユーザープロジェクト一覧結果。
 */
@ToString
@EqualsAndHashCode
public class UserProjectListResultV1 implements UserProjectListResult {

    /**
     * ユーザープロジェクト一覧。
     */
    private final List<UserProject> userProjects = new ArrayList<>();

    /**
     * ユーザープロジェクト一覧を追加する。
     *
     * @param userProject ユーザープロジェクト。
     */
    public void add(final UserProject userProject) {
        this.userProjects.add(userProject);
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<UserProject> getUserProjectList() {
        return userProjects;
    }

    /**
     * V1 APIを使用した時のレスポンスJsonをパースする。
     *
     * @param rawJson 生のjsonレスポンス文字列。
     * @return V1用のユーザープロジェクト一覧結果。
     */
    public static UserProjectListResultV1 parseUserProjectsJsonV1(final String rawJson) {
        final JsonParser parser = Json.createParser(new StringReader(rawJson));
        // 必要な情報のみを取得したいため、ネストされたjsonを見ない処理をするための変数。
        int nestedObjectCount = -1;
        final UserProjectListResultV1 result = new UserProjectListResultV1();
        UserProjectV1 userProject = null;
        String key = null;
        while (parser.hasNext()) {
            final JsonParser.Event event = parser.next();
            switch (event) {
                case KEY_NAME:
                    if (nestedObjectCount > 0) {
                        continue;
                    }
                    key = parser.getString();
                    System.out.printf("keyName:%s\n", key);
                    break;
                case VALUE_STRING:
                    if (nestedObjectCount > 0) {
                        continue;
                    }
                    final String strValue = parser.getString();
                    switch (defaultString(key)) {
                        case "name":
                            userProject.setName(strValue);
                            break;
                        default:
                            break;
                    }
                    break;
                case VALUE_NUMBER:
                    if (nestedObjectCount > 0) {
                        continue;
                    }
                    final long numValue = parser.getLong();
                    switch (defaultString(key)) {
                        case "id":
                            userProject.setId(Long.toString(numValue));
                            break;
                        default:
                            break;
                    }
                    break;
                case START_ARRAY:
                    System.out.println("START_ARRAY");
                    userProject = new UserProjectV1();
                    break;
                case END_ARRAY:
                    System.out.println("END_ARRAY");
                    result.add(userProject);
                    break;
                case START_OBJECT:
                    ++nestedObjectCount;
                    break;
                case END_OBJECT:
                    --nestedObjectCount;
                    break;
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NULL:
                    break;
                default:
                    break;
            }
        }
        return result;
    }
}
