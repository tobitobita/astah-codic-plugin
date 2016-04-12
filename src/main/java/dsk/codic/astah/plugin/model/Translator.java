package dsk.codic.astah.plugin.model;

import dsk.codic.service.CodicService;
import static dsk.utils.SystemHelper.getEnv;
import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * 翻訳者。
 */
public class Translator {

    public static final String CODIC_ACCESS_TOKEN = "CODIC_ACCESS_TOKEN";
    public static final String CODIC_PROJECT_ID = "CODIC_PROJECT_ID";

    /**
     * 対象の文字列。
     */
    private StringBuilder targetText;
    /**
     * 選択開始位置。
     */
    private int selectionStart;
    /**
     * 選択終了位置。
     */
    private int selectionEnd;
    /**
     * 選択した文字列。
     */
    private String selectedText;
    /**
     * 翻訳した文字列。
     */
    private String translatedText;
    /**
     * 翻訳サービス。
     */
    private final CodicService service = new CodicService();

    /**
     * 準備する。
     *
     * @param targetText 対象の文字列。
     * @param selectionStart 選択開始位置。
     * @param selectionEnd 選択終了位置。
     * @param selectedText 選択した文字列。
     */
    public void setup(final String targetText, final int selectionStart, final int selectionEnd, final String selectedText) {
        this.targetText = new StringBuilder(targetText);
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
        this.selectedText = selectedText;
        this.translatedText = null;
    }

    /**
     * クリアする。
     */
    public void clear() {
        this.targetText = null;
        this.selectionStart = 0;
        this.selectionEnd = 0;
        this.selectedText = null;
        this.translatedText = null;
    }

    /**
     * 翻訳する。
     *
     * @param casing 記法。
     */
    public void translate(final String casing) {
        if (this.translatedText != null) {
            return;
        }
        System.out.printf("start:%d, end:%d\n", this.selectionStart, this.selectionEnd);
        // 選択部分を削除。
        this.targetText.delete(this.selectionStart, this.selectionEnd);
        // WEB APIを呼び、翻訳した文字列を取得。
        this.translatedText = this.service
                .translate(getEnv(CODIC_ACCESS_TOKEN),
                        this.selectedText,
                        getEnv(CODIC_PROJECT_ID),
                        casing)
                .getTranslatedText();
        // 選択開始部分から翻訳した文字列を挿入。
        this.targetText.insert(this.selectionStart, this.translatedText);
        // 選択終了位置を翻訳した文字列を元に更新。
        this.selectionEnd = this.selectionStart + this.translatedText.length();
    }

    /**
     * 選択した文字列が同じか。
     *
     * @param newSelectedText 新たに選択した文字列。
     * @return 同じ場合はtrueを返す。
     */
    public boolean isSameSelectedText(final String newSelectedText) {
        return defaultString(this.selectedText).equals(newSelectedText);
    }

    /**
     * 対象の文字列を取得する。
     *
     * @return 対象の文字列。
     */
    public String getTargetText() {
        return this.targetText.toString();
    }

    /**
     * 選択した文字列を取得する。
     *
     * @return 選択した文字列。
     */
    public String getSelectedText() {
        return this.selectedText;
    }

    /**
     * 翻訳した文字列を取得する。
     *
     * @return 翻訳した文字列。
     */
    public String getTranslatedText() {
        return this.translatedText;
    }

    /**
     * 選択開始位置を取得する。
     *
     * @return 選択開始位置。
     */
    public int getSelectionStart() {
        return this.selectionStart;
    }

    /**
     * 選択終了位置を取得する。
     *
     * @return 選択終了位置。
     */
    public int getSelectionEnd() {
        return this.selectionEnd;
    }
}
