package dsk.codic.astah.plugin.ui;

import static dsk.codic.astah.plugin.model.Translator.CODIC_ACCESS_TOKEN;
import static dsk.codic.astah.plugin.model.Translator.CODIC_PROJECT_ID;
import dsk.codic.astah.plugin.ui.entity.ProjectData;
import dsk.codic.astah.plugin.ui.entity.SettingDialogData;
import dsk.codic.service.CodicService;
import java.awt.BorderLayout;
import static java.awt.BorderLayout.CENTER;
import java.awt.Dimension;
import static java.awt.EventQueue.invokeLater;
import java.awt.FlowLayout;
import static java.awt.FlowLayout.RIGHT;
import java.awt.Window;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static javax.swing.SwingUtilities.computeStringWidth;
import javax.xml.bind.JAXB;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BoxLayout.LINE_AXIS;
import static javax.swing.BoxLayout.PAGE_AXIS;
import javax.swing.JDialog;

public class SettingDialog extends JDialog {

    private static final long serialVersionUID = -3750785845370268505L;

    private static final Path DIR_PATH = Paths.get(System.getProperty("user.home"), ".astah", "astah_codic_plugin");
    private static final Path FILE_PATH = Paths.get(DIR_PATH.toUri().getPath(), "setting.xml");

    private JTextField accessToken;
    private JComboBox<String> projectList;
    private JButton saveButton;

    private int maxLabelWidth;
    private int fontSize;

    private SettingDialogData settingData;

    private final CodicService service = new CodicService();

    public SettingDialog(final Window owner, final boolean modal) {
        super(owner, "codic設定");
        this.build(owner, modal);
        load();
        update();
    }

    private void build(final Window owner, final boolean modal) {
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setModal(modal);
        this.setResizable(false);
        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, PAGE_AXIS));
        p.add(this.buildAccessTokenPanel());
        p.add(this.buildProjectListPanel());
        p.add(this.buildButtonAreaPanel());
        this.setLayout(new BorderLayout());
        this.add(p, CENTER);
        this.pack();
        this.setLocationRelativeTo(owner);
    }

    private JPanel buildAccessTokenPanel() {
        final JPanel basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, LINE_AXIS));
        final String labelText = "アクセストークン:";
        final JLabel label = new JLabel(labelText);
        fontSize = label.getFont().getSize();
        this.maxLabelWidth = computeStringWidth(label.getFontMetrics(label.getFont()), labelText);
        label.setPreferredSize(new Dimension(this.maxLabelWidth, label.getPreferredSize().height));
        basePanel.add(label);
        this.accessToken = new JTextField();
        final int width = computeStringWidth(label.getFontMetrics(label.getFont()), "0123456789012345678901234567890123456789");
        this.accessToken.setPreferredSize(new Dimension(width, this.accessToken.getPreferredSize().height));
        basePanel.add(this.accessToken);
        final JButton verifyButton = new JButton("検証");
        verifyButton.addActionListener(e -> {
            System.out.println("検証");
            projectList.setEnabled(false);
            saveButton.setEnabled(false);
            settingData.setAccessToken(accessToken.getText());
            settingData.getProjects().clear();
            service.getUserProjects(accessToken.getText()).getUserProjectList().stream()
                    .forEach(project -> {
                        settingData.addProject(new ProjectData(project.getId(), project.getName()));
                    });
            update();
        });
        basePanel.add(verifyButton);
        basePanel.setBorder(createEmptyBorder(fontSize, fontSize, fontSize / 2, fontSize));
        return basePanel;
    }

    private JPanel buildProjectListPanel() {
        final JPanel basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, LINE_AXIS));
        final JLabel label = new JLabel("プロジェクト:");
        label.setPreferredSize(new Dimension(this.maxLabelWidth, label.getPreferredSize().height));
        basePanel.add(label);
        this.projectList = new JComboBox<>();
        this.projectList.setEditable(false);
        this.projectList.setEnabled(false);
        basePanel.add(this.projectList);
        basePanel.setBorder(createEmptyBorder(fontSize / 2, fontSize, fontSize / 2, fontSize));
        return basePanel;
    }

    private JPanel buildButtonAreaPanel() {
        final JPanel basePanel = new JPanel();
        basePanel.setLayout(new FlowLayout(RIGHT));
        saveButton = new JButton("OK");
        saveButton.addActionListener(e -> {
            save();
            setVisible(false);
        });
        saveButton.setEnabled(false);
        basePanel.add(saveButton);
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            setVisible(false);
        });
        basePanel.add(cancelButton);
        basePanel.setBorder(createEmptyBorder(fontSize / 2, fontSize, 0, fontSize));
        return basePanel;
    }

    private void load() {
        System.out.println("dsk.codic.astah.plugin.ui.SettingDialog.load()");
        if (!Files.exists(FILE_PATH)) {
            this.settingData = new SettingDialogData();
            return;
        }
        try (final InputStream is = new FileInputStream(FILE_PATH.toFile())) {
            this.settingData = JAXB.unmarshal(is, SettingDialogData.class);
            // システムプロパティで受け渡しする。
            this.setSystemProperty();
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    private void save() {
        System.out.println("dsk.codic.astah.plugin.ui.SettingDialog.save()");
        this.settingData.setAccessToken(this.accessToken.getText());
        this.settingData.setSelectedProjectName(this.projectList.getItemAt(this.projectList.getSelectedIndex()));
        try {
            if (!Files.exists(DIR_PATH)) {
                Files.createDirectories(DIR_PATH);
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
        try (final OutputStream os = new FileOutputStream(FILE_PATH.toFile())) {
            JAXB.marshal(this.settingData, os);
            // システムプロパティで受け渡しする。
            this.setSystemProperty();
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    private void setSystemProperty() {
        System.setProperty(CODIC_ACCESS_TOKEN, this.settingData.getAccessToken());
        System.setProperty(CODIC_PROJECT_ID, this.settingData.getSelectedProjectId());
    }

    private void update() {
        this.projectList.removeAllItems();
        this.settingData.getProjects().stream()
                .forEach(projectData -> {
                    projectList.addItem(projectData.getName());
                });
        this.accessToken.setText(this.settingData.getAccessToken());
        if (this.projectList.getItemCount() <= 0) {
            return;
        }
        if (this.settingData.getSelectedProjectName() == null) {
            this.projectList.setSelectedIndex(0);
        } else {
            this.projectList.setSelectedItem(this.settingData.getSelectedProjectName());
        }
        this.projectList.setEnabled(true);
        this.saveButton.setEnabled(true);
    }

    public static void main(String[] args) {
        invokeLater(() -> {
            new SettingDialog(null, false).setVisible(true);
        });
    }
}
