package dsk.codic.model.impl;

import dsk.codic.model.UserProject;
import lombok.Data;

/**
 * V1 API を使ったときのユーザープロジェクト。
 */
@Data
public class UserProjectV1 implements UserProject {

    /**
     * プロジェクトId。
     */
    private String id;
    /**
     * プロジェクト名。
     */
    private String name;
}
