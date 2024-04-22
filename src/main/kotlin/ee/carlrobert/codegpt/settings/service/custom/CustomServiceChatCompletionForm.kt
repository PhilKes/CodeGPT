package ee.carlrobert.codegpt.settings.service.custom

import com.intellij.openapi.ui.MessageType
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.completions.CompletionRequestProvider
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.ui.OverlayUtil
import ee.carlrobert.llm.client.openai.completion.ErrorDetails
import ee.carlrobert.llm.completion.CompletionEventListener
import okhttp3.sse.EventSource
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities

class CustomServiceChatCompletionForm(state: CustomServiceChatCompletionSettingsState) {

    private val urlField = JBTextField(state.url, 30)
    private val tabbedPane = CustomServiceFormTabbedPane(state.headers, state.body)
    private val testConnectionButton = JButton(
        CodeGPTBundle.get("settingsConfigurable.service.custom.openai.testConnection.label")
    )

    init {
        testConnectionButton.addActionListener { testConnection() }
    }

    var url: String
        get() = urlField.text
        set(url) {
            urlField.text = url
        }

    var headers: MutableMap<String, String>
        get() = tabbedPane.headers
        set(value) {
            tabbedPane.headers = value
        }

    var body: MutableMap<String, Any>
        get() = tabbedPane.body
        set(value) {
            tabbedPane.body = value
        }

    val form: JPanel
        get() = FormBuilder.createFormBuilder()
            .addVerticalGap(8)
            .addLabeledComponent(
                CodeGPTBundle.get("settingsConfigurable.service.custom.openai.url.label"),
                JPanel(BorderLayout(8, 0)).apply {
                    add(urlField, BorderLayout.CENTER)
                    add(testConnectionButton, BorderLayout.EAST)
                }
            )
            .addComponent(tabbedPane)
            .addComponentFillVertically(JPanel(), 0)
            .panel

    fun resetForm(settings: CustomServiceChatCompletionSettingsState) {
        urlField.text = settings.url
        tabbedPane.headers = settings.headers
        tabbedPane.body = settings.body
    }

    private fun testConnection() {
        CompletionRequestService.getInstance().getCustomOpenAIChatCompletionAsync(
            CompletionRequestProvider.buildCustomOpenAICompletionRequest("Hello!"),
            TestConnectionEventListener()
        )
    }

    internal inner class TestConnectionEventListener : CompletionEventListener<String?> {
        override fun onMessage(value: String?, eventSource: EventSource) {
            if (!value.isNullOrEmpty()) {
                SwingUtilities.invokeLater {
                    OverlayUtil.showBalloon(
                        CodeGPTBundle.get("settingsConfigurable.service.custom.openai.connectionSuccess"),
                        MessageType.INFO,
                        testConnectionButton
                    )
                    eventSource.cancel()
                }
            }
        }

        override fun onError(error: ErrorDetails, ex: Throwable) {
            SwingUtilities.invokeLater {
                OverlayUtil.showBalloon(
                    CodeGPTBundle.get("settingsConfigurable.service.custom.openai.connectionFailed")
                            + "\n\n"
                            + error.message,
                    MessageType.ERROR,
                    testConnectionButton
                )
            }
        }
    }
}