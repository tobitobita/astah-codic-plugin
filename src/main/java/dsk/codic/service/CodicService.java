package dsk.codic.service;

import dsk.codic.model.EngineResult;
import static dsk.codic.model.impl.EngineResultV1.parseEngineJsonV1;
import static dsk.codic.model.impl.UserProjectListResultV1.parseUserProjectsJsonV1;
import static dsk.utils.SystemHelper.getEnv;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import dsk.codic.model.UserProjectListResult;
import static java.lang.String.format;
import static javax.ws.rs.client.ClientBuilder.newClient;

/**
 * codic のAPIを使うサービス。
 */
public class CodicService {

    private static final String TARGET_URL = "https://api.codic.jp";

    /**
     * 翻訳する。
     *
     * @param accessToken codic api に対して認証を行うためのアクセストークン。
     * @param text 翻訳する文字列。
     * @param projectId codic で作成したプロジェクトのId。プロジェクト一覧を取得するとその中にIdがある。
     * @param casing 変換したあとのケース変換ルール。
     * @return 結果のJsonをパースし、必要なもののみ公開しているインターフェイス。
     */
    public EngineResult translate(final String accessToken, final String text, final String projectId, final String casing) {
        final Response res = newClient()
                .target(TARGET_URL)
                .path("/v1/engine/translate.json")
                .queryParam("project_id", projectId)
                .queryParam("casing", casing)
                .queryParam("text", text)
                .request(APPLICATION_JSON)
                .header(AUTHORIZATION, format("Bearer %s", accessToken))
                .get();
        res.bufferEntity();
        return parseEngineJsonV1(res.readEntity(String.class));
    }

    /**
     * ユーザープロジェクト一覧を取得する。
     *
     * @param accessToken codic api に対して認証を行うためのアクセストークン。
     * @return 結果のJsonをパースし、必要なもののみ公開しているインターフェイス。
     */
    public UserProjectListResult getUserProjects(final String accessToken) {
        final Response res = newClient()
                .target(TARGET_URL)
                .path("/v1/user_projects.json")
                .request(APPLICATION_JSON)
                .header(AUTHORIZATION, format("Bearer %s", accessToken))
                .get();
        res.bufferEntity();
        return parseUserProjectsJsonV1(res.readEntity(String.class));
    }

    public static void main(String[] args) {
        System.out.println(new CodicService().getUserProjects(getEnv("CODIC_ACCESS_TOKEN")));
    }
}
