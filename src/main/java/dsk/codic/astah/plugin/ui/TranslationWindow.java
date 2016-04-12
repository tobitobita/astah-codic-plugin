package dsk.codic.astah.plugin.ui;

import dsk.codic.astah.plugin.model.Translator;
import static dsk.utils.SystemHelper.isMacOs;
import static java.awt.AWTEvent.FOCUS_EVENT_MASK;
import static java.awt.AWTEvent.KEY_EVENT_MASK;
import static java.awt.AWTEvent.MOUSE_EVENT_MASK;
import java.awt.BorderLayout;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.WEST;
import java.awt.Color;
import static java.awt.Color.BLACK;
import java.awt.Dimension;
import static java.awt.EventQueue.invokeLater;
import static java.awt.Toolkit.getDefaultToolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.META_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_C;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import javafx.scene.text.Font;
import javax.swing.BorderFactory;
import static javax.swing.Box.createGlue;
import javax.swing.BoxLayout;
import static javax.swing.BoxLayout.LINE_AXIS;
import static javax.swing.BoxLayout.PAGE_AXIS;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import static javax.swing.SwingConstants.BOTTOM;
import static javax.swing.SwingUtilities.computeStringWidth;
import javax.swing.text.JTextComponent;

/**
 * 変換を行うウインドウ。
 */
public class TranslationWindow extends JDialog {

    private static final long serialVersionUID = -6519019039034462198L;

    /**
     * キーボード、マウスのイベントを受け持つ。
     */
    private final AWTEventListener eventListener;

    /**
     * 選択文字列を表示するラベル。
     */
    private JTextField selectedTextLabel;

    /**
     * 変換した文字列を表示するラベル。
     */
    private JTextField translatedTextLabel;

    /**
     * 変換タイプの一覧。
     */
    private JComboBox<CaseType> caseList;
    /**
     * 対象となるテキストコンポーネントの参照。
     */
    private JTextComponent targetTextComponent;

    /**
     * 変換を行うモデル。
     */
    private Translator translator;

    /**
     * codicの設定ダイアログ。
     */
    private final SettingDialog settingDialog;

    /**
     *
     */
    private int maxLabelWidth;
    /**
     * フォントサイズ。
     */
    private int fontSize;

    /**
     * コンストラクタ。
     *
     * @param owner オーナーウインドウ。
     */
    public TranslationWindow(Window owner) {
        super(owner);
        // オーナーはあくまでもastahのWindow。
        this.settingDialog = new SettingDialog(owner, true);
        this.eventListener = event -> {
            // イベントをフィルタリング。
            boolean invalidEvent;
            switch (event.getID()) {
                case MouseEvent.MOUSE_MOVED:
                case MouseEvent.MOUSE_ENTERED:
                case MouseEvent.MOUSE_EXITED:
                case MouseEvent.MOUSE_DRAGGED:
                    invalidEvent = true;
                    break;
                default:
                    invalidEvent = false;
                    break;
            }
            if (invalidEvent) {
                return;
            }
            // テキストコンポーネントのみ対象とする。
            if (!(event.getSource() instanceof JTextComponent)) {
                return;
            }
            this.targetTextComponent = null;
            this.translator.clear();
            final JTextComponent textComponent = (JTextComponent) event.getSource();
            // 表示されており、有効で編集可能なものを対象。
            if (!(textComponent.isEnabled() && textComponent.isEditable() && textComponent.isVisible())) {
                return;
            }
            this.targetTextComponent = textComponent;
            System.out.println("set targetTextComponent.");
            // キーボードショートカットを実現する。
            // キーボードイベントをフィルタリング。
            if (!(event instanceof KeyEvent)) {
                return;
            }
            final KeyEvent e = (KeyEvent) event;
            final int modifiers = e.getModifiersEx();
            // Shift + Ctrl + C
            // TODO キーボードショートカットはカスタマイズできるよう変更する。
            if ((modifiers & SHIFT_DOWN_MASK) == 0 || e.getKeyCode() != VK_C) {
                return;
            }
            // MacはCtrlをMetaキーとする。
            if (isMacOs()) {
                if ((modifiers & META_DOWN_MASK) == 0) {
                    return;
                }
            } else if ((modifiers & CTRL_DOWN_MASK) == 0) {
                return;
            }
            // イベント消費。
            e.consume();
            // 一気通貫。
            setTranslateText();
            translator.translate(this.caseList.getItemAt(this.caseList.getSelectedIndex()).getValue());
            update();
            setTranslatedText2TargetTextComponent();
        };
        this.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setVisible(final boolean visible) {
        // 初期化処理。
        this.init();
        this.update();
        super.setVisible(visible);
        // イベント登録・解除を行う。
        invokeLater(() -> {
            if (visible) {
                getDefaultToolkit().addAWTEventListener(eventListener, MOUSE_EVENT_MASK | FOCUS_EVENT_MASK | KEY_EVENT_MASK);
            } else {
                getDefaultToolkit().removeAWTEventListener(eventListener);
            }
        });
    }

    /**
     * 構築する。
     */
    private void build() {
        System.out.println("dsk.codic.astah.plugin.ui.TranslationWindow.build()");
        this.setTitle("codic");
        this.setResizable(false);
        this.fontSize = new BigDecimal(Font.getDefault().getSize()).intValue();
        this.setLayout(new BorderLayout());
        this.add(this.createToolBar(), NORTH);
        this.add(this.createLabelPanel(), CENTER);
        this.pack();
    }

    private JToolBar createToolBar() {
        final JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        final JButton getButton = this.createToolbarButton("取得");
        getButton.addActionListener(e -> {
            setTranslateText();
            update();
        });
        final JButton translationButton = this.createToolbarButton("変換");
        translationButton.addActionListener(e -> {
            translator.translate(this.caseList.getItemAt(this.caseList.getSelectedIndex()).getValue());
            update();
        });
        final JButton updateButton = this.createToolbarButton("更新");
        updateButton.addActionListener(e -> {
            setTranslatedText2TargetTextComponent();
        });
        final JButton settingButton = this.createToolbarButton("設定");
        settingButton.addActionListener(e -> {
            settingDialog.setVisible(true);
        });
        toolbar.add(getButton);
        toolbar.add(translationButton);
        toolbar.add(updateButton);
        toolbar.add(createGlue());
        toolbar.add(settingButton);
        return toolbar;
    }

    private JButton createToolbarButton(final String text) {
        final JButton b = new JButton(text);
        b.setToolTipText(text);
        b.setVerticalTextPosition(BOTTOM);
        return b;
    }

    private JPanel createLabelPanel() {
        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, PAGE_AXIS));
        p.add(this.createSelectedLabelPanel());
        p.add(this.createTranslatedLabelPanel());
        p.add(this.createSelectCasingPanel());
        return p;
    }

    private JPanel createSelectedLabelPanel() {
        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, LINE_AXIS));
        final String labelText = "選択した文字列:";
        final JLabel label = new JLabel(labelText);
        p.add(label);
        this.maxLabelWidth = computeStringWidth(label.getFontMetrics(label.getFont()), labelText);
        this.selectedTextLabel = new JTextField();
        this.modifyTextField(this.selectedTextLabel, label.getBackground());
        p.add(this.selectedTextLabel);
        p.setBorder(BorderFactory.createEmptyBorder(fontSize, fontSize, fontSize / 2, fontSize));
        return p;
    }

    private JPanel createTranslatedLabelPanel() {
        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, LINE_AXIS));
        final JLabel label = new JLabel("翻訳した文字列:");
        p.add(label);
        this.translatedTextLabel = new JTextField();
        this.modifyTextField(this.translatedTextLabel, label.getBackground());
        p.add(this.translatedTextLabel);
        p.setBorder(BorderFactory.createEmptyBorder(fontSize / 2, fontSize, fontSize / 2, fontSize));
        return p;
    }

    private void modifyTextField(final JTextField textField, final Color background) {
        textField.setEditable(false);
        textField.setBackground(background);
        textField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        textField.setPreferredSize(new Dimension(300, textField.getPreferredSize().height));
    }

    private JPanel createSelectCasingPanel() {
        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, LINE_AXIS));
        final JLabel label = new JLabel("記法:");
        label.setPreferredSize(new Dimension(this.maxLabelWidth, label.getPreferredSize().height));
        p.add(label);
        this.caseList = new JComboBox<>();
        this.caseList.addItem(new CaseType("camel", "camelCase"));
        this.caseList.addItem(new CaseType("pascal", "PascalCase"));
        this.caseList.addItem(new CaseType("lower underscore", "snake_case"));
        this.caseList.addItem(new CaseType("upper underscore", "SNAKE_CASE"));
        this.caseList.addItem(new CaseType("hyphen", "ハイフネーション（get-value）"));
        // TODO 永続化したい
        this.caseList.setSelectedIndex(0);
        this.caseList.setMaximumSize(new Dimension(250, this.caseList.getPreferredSize().height));
        p.add(this.caseList);
        p.setBorder(BorderFactory.createEmptyBorder(fontSize / 2, fontSize, fontSize, fontSize));
        return p;
    }

    /**
     * 初期化する。
     */
    private void init() {
        System.out.println("dsk.codic.astah.plugin.ui.TranslationWindow.init()");
        this.targetTextComponent = null;
        this.translator = new Translator();
    }

    /**
     * 更新する。<br>
     * TODO モデルのイベントドリブンでupdateするよう変更する。
     */
    private void update() {
        System.out.println("dsk.codic.astah.plugin.ui.TranslationWindow.update()");
        this.selectedTextLabel.setText(this.translator.getSelectedText());
        this.translatedTextLabel.setText(this.translator.getTranslatedText());
        this.pack();
    }

    /**
     * 翻訳する文字列を設定する。
     */
    private void setTranslateText() {
        if (this.targetTextComponent == null) {
            return;
        }
        // 対象が同じ文字列の場合は設定しない。
        if (translator.isSameSelectedText(targetTextComponent.getSelectedText())) {
            return;
        }
        System.out.println("dsk.codic.astah.plugin.ui.TranslationWindow.setTranslator()");
        translator.setup(targetTextComponent.getText(),
                targetTextComponent.getSelectionStart(),
                targetTextComponent.getSelectionEnd(),
                targetTextComponent.getSelectedText());
    }

    /**
     * テキストコンポーネントへ翻訳した文字列を設定する。
     */
    private void setTranslatedText2TargetTextComponent() {
        if (targetTextComponent == null) {
            return;
        }
        System.out.println("dsk.codic.astah.plugin.ui.TranslationWindow.setTranslatedText()");
        targetTextComponent.setText(translator.getTargetText());
        targetTextComponent.setSelectionStart(translator.getSelectionStart());
        targetTextComponent.setSelectionEnd(translator.getSelectionEnd());
        invokeLater(() -> {
            targetTextComponent.requestFocus();
        });
    }

    public static void main(String[] args) {
        invokeLater(() -> {
            final TranslationWindow w = new TranslationWindow(null);
            w.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            w.setVisible(true);
        });
    }
}
