package dsk.codic.model;

import java.util.List;

/**
 * ユーザープロジェクト一覧の結果。
 */
public interface UserProjectListResult {

    /**
     * ユーザープロジェクト一覧を取得する。
     *
     * @return ユーザープロジェクト一覧。
     */
    List<UserProject> getUserProjectList();
}
