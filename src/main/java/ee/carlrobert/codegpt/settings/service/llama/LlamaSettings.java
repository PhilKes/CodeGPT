package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.LLAMA_API_KEY;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaSettingsForm;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT", storages = @Storage("CodeGPT_LlamaSettings.xml"))
public class LlamaSettings implements PersistentStateComponent<LlamaSettingsState> {

  private LlamaSettingsState state = new LlamaSettingsState();

  @Override
  @NotNull
  public LlamaSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull LlamaSettingsState state) {
    this.state = state;
  }

  public static LlamaSettingsState getCurrentState() {
    return getInstance().getState();
  }

  /**
   * A model with InfillPromptTemplate is selected when running a local server
   * or an InfillPromptTemplate is selected when using a remote server.
   */
  public static boolean isCodeCompletionsPossible() {
    LlamaSettingsState state = getInstance().getState();
    if (state.isRunLocalServer()) {
      return LlamaModel.findByHuggingFaceModel(state.getHuggingFaceModel())
          .getInfillPromptTemplate() != null;
    }
    return state.getRemoteModelInfillPromptTemplate() != null;
  }

  public static LlamaSettings getInstance() {
    return ApplicationManager.getApplication().getService(LlamaSettings.class);
  }

  public boolean isModified(LlamaSettingsForm form) {
    return !form.getCurrentState().equals(state)
        || !StringUtils.equals(
        form.getLlamaServerPreferencesForm().getApiKey(),
        CredentialsStore.INSTANCE.getCredential(LLAMA_API_KEY));
  }
}
