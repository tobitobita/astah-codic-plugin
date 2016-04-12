package dsk.codic.model;

/**
 * エンジンAPIの結果。
 */
public interface EngineResult {

    /**
     * 文字列を設定する。
     *
     * @param text 文字列。
     */
    void setText(String text);

    /**
     * 成功したか。
     *
     * @return 成功した場合はtrueを返す。
     */
    boolean isSuccess();

    /**
     * 翻訳した文字列を取得する。
     *
     * @return 翻訳した文字列。
     */
    String getTranslatedText();
}
