package ee.carlrobert.codegpt.settings.state;

import static java.util.stream.Collectors.toList;

import ee.carlrobert.codegpt.completions.LlmModel;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.completions.llama.LlamaLocalSettings;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LocalSettings<T extends LlmModel> extends CommonSettings {

  private Integer serverPort = getRandomAvailablePortOrDefault();
  private int contextSize = 2048;
  private int threads = 8;
  private String additionalCompileParameters = "";

  private boolean useCustomModel = false;
  private String customModel = "";
  private T llmModel;

  public LocalSettings(T defaultLlmModel) {
    this.llmModel = defaultLlmModel;
  }

  public LocalSettings(boolean useCustomModel, String customModel,
      T llmModel,
      PromptTemplate promptTemplate, Integer serverPort, int contextSize, int threads,
      String additionalCompileParameters) {
    super(promptTemplate);
    this.useCustomModel = useCustomModel;
    this.customModel = customModel;
    this.llmModel = llmModel;
    this.serverPort = serverPort;
    this.contextSize = contextSize;
    this.threads = threads;
    this.additionalCompileParameters = additionalCompileParameters;
  }


  public boolean isModified(LlamaLocalSettings localSettings) {
    return super.isModified(localSettings)
        || useCustomModel != isUseCustomModel()
        || !customModel.equals(getCustomModel())
        || !llmModel.equals(getLlModel())
        || !serverPort.equals(localSettings.getServerPort())
        || contextSize != localSettings.getContextSize()
        || threads != localSettings.getThreads()
        || !additionalCompileParameters.equals(localSettings.getAdditionalParameters());
  }

  private static Integer getRandomAvailablePortOrDefault() {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException e) {
      return 8080;
    }
  }

  public Integer getServerPort() {
    return serverPort;
  }

  public void setServerPort(Integer serverPort) {
    this.serverPort = serverPort;
  }

  public int getContextSize() {
    return contextSize;
  }

  public void setContextSize(int contextSize) {
    this.contextSize = contextSize;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public String getAdditionalParameters() {
    return additionalCompileParameters;
  }

  public void setAdditionalParameters(String additionalParameters) {
    this.additionalCompileParameters = additionalParameters;
  }

  public List<String> getListOfAdditionalParameters() {
    if (additionalCompileParameters.trim().isEmpty()) {
      return Collections.emptyList();
    }
    var parameters = additionalCompileParameters.split(",");
    return Arrays.stream(parameters)
        .map(String::trim)
        .collect(toList());
  }

  public boolean isUseCustomModel() {
    return useCustomModel;
  }

  public void setUseCustomModel(boolean useCustomModel) {
    this.useCustomModel = useCustomModel;
  }

  public String getCustomModel() {
    return customModel;
  }

  public void setCustomModel(String customModel) {
    this.customModel = customModel;
  }

  public T getLlModel() {
    return llmModel;
  }

  public void setLlmModel(T llamaHuggingFaceModel) {
    this.llmModel = llamaHuggingFaceModel;
  }

}
