package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.ui.UIUtil.createForm;

import com.intellij.ui.components.JBRadioButton;
import ee.carlrobert.codegpt.completions.ServerAgent;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.ui.UIUtil.RadioButtonWithLayout;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Form containing {@link JBRadioButton} to toggle between({@link LlamaLocalServiceForm}) or
 * ({@link LlamaRemoteServiceForm}).
 */
public abstract class LlamaLocalOrRemoteServiceForm {

  private static final String RUN_LOCAL_SERVER_FORM_CARD_CODE = "RunLocalServerSettings";
  private static final String USE_EXISTING_SERVER_FORM_CARD_CODE = "UseExistingServerSettings";

  private final JBRadioButton runLocalServerRadioButton;
  private final JBRadioButton useExistingServerRadioButton;

  private final LlamaLocalServiceForm llamaLocalServiceForm;
  private final LlamaRemoteServiceForm llamaRemoteServiceForm;

  private final LlamaSettingsState llamaSettingsState;

  public LlamaLocalOrRemoteServiceForm(LlamaSettingsState llamaSettingsState,
      ServerAgent serverAgent,
      ServiceType serviceType) {
    this.llamaSettingsState = llamaSettingsState;
    runLocalServerRadioButton = new JBRadioButton("Run local server",
        llamaSettingsState.isRunLocalServer());
    useExistingServerRadioButton = new JBRadioButton("Use existing server",
        !llamaSettingsState.isRunLocalServer());

    llamaLocalServiceForm = new LlamaLocalServiceForm(llamaSettingsState.getLocalSettings(),
        serverAgent, this::onServerAgentStateChanged) {
      @Override
      protected boolean isModelExists(HuggingFaceModel model) {
        return LlamaLocalOrRemoteServiceForm.this.isModelExists(model);
      }
    };
    llamaRemoteServiceForm = new LlamaRemoteServiceForm();
  }

  private void onServerAgentStateChanged(boolean isRunning) {
    setFormEnabled(isRunning);
    llamaSettingsState.getLocalSettings().setServerRunning(isRunning);
  }

  public abstract boolean isModelExists(HuggingFaceModel model);


  public JPanel getForm() {
    return createForm(Map.of(
        RUN_LOCAL_SERVER_FORM_CARD_CODE, new RadioButtonWithLayout(runLocalServerRadioButton,
            llamaLocalServiceForm.getPanel()),
        USE_EXISTING_SERVER_FORM_CARD_CODE, new RadioButtonWithLayout(useExistingServerRadioButton,
            llamaRemoteServiceForm.getPanel())
    ), runLocalServerRadioButton.isSelected()
        ? RUN_LOCAL_SERVER_FORM_CARD_CODE
        : USE_EXISTING_SERVER_FORM_CARD_CODE);
  }


  private void setFormEnabled(boolean enabled) {
    runLocalServerRadioButton.setEnabled(enabled);
    useExistingServerRadioButton.setEnabled(enabled);
    llamaLocalServiceForm.setFormEnabled(enabled);
  }

  public void setRunLocalServer(boolean runLocalServer) {
    runLocalServerRadioButton.setSelected(runLocalServer);
  }

  public boolean isRunLocalServer() {
    return runLocalServerRadioButton.isSelected();
  }

  public void setRemoteSettings(LlamaRemoteSettings remoteSettings) {
    llamaRemoteServiceForm.setRemoteSettings(remoteSettings);
  }

  public LlamaRemoteSettings getRemoteSettings() {
    return llamaRemoteServiceForm.getRemoteSettings();
  }

  public void setLocalSettings(LlamaLocalSettings localSettings) {
    llamaLocalServiceForm.setLocalSettings(localSettings);
  }

  public LlamaLocalSettings getLocalSettings() {
    return llamaLocalServiceForm.getLocalSettings();
  }

  public String getLocalApiKey() {
    return llamaLocalServiceForm.getApiKey();
  }

  public String getRemoteApikey() {
    return llamaRemoteServiceForm.getApiKey();
  }
}
