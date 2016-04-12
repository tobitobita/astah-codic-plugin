package dsk.codic.astah.plugin;

import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import dsk.codic.astah.plugin.ui.TranslationWindow;

public class TemplateAction implements IPluginActionDelegate {

    private TranslationWindow dialog;

    @Override
    public Object run(IWindow window) throws UnExpectedException {
        if (dialog == null) {
            dialog = new TranslationWindow(window.getParent());
        }
        if (!dialog.isVisible()) {
            dialog.setVisible(true);
        }
        return null;
    }
}
