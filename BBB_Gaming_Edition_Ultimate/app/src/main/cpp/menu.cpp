#include <jni.h>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include "imgui.h"
#include "imgui_impl_opengl3.h"
#include "imgui_impl_android.h"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "ABK_MENU", VA_ARGS)

static EGLDisplay g_EglDisplay = EGL_NO_DISPLAY;
static EGLSurface g_EglSurface = EGL_NO_SURFACE;
static EGLContext g_EglContext = EGL_NO_CONTEXT;
static ANativeWindow* g_Window = nullptr;

// متغيرات الهاك
bool esp_enabled = true;
bool aimbot_enabled = false;
float aim_fov = 5.0f;

void InitImGui(int width, int height) {
IMGUI_CHECKVERSION();
ImGui::CreateContext();
ImGuiIO& io = ImGui::GetIO();
io.DisplaySize = ImVec2(width, height);

ImGui_ImplAndroid_Init(g_Window);
ImGui_ImplOpenGL3_Init("#version 300 es");
}

void RenderMenu() {
if (!g_Window) return;

ImGui_ImplOpenGL3_NewFrame();
ImGui_ImplAndroid_NewFrame();
ImGui::NewFrame();

ImGui::SetNextWindowSize(ImVec2(500, 400), ImGuiCond_FirstUseEver);
ImGui::Begin("BBB Menu - CODM", nullptr, ImGuiWindowFlags_NoCollapse);

if (ImGui::BeginTabBar("Tabs")) {
    if (ImGui::BeginTabItem("ESP")) {
            ImGui::Checkbox("Enable ESP", &esp_enabled);
                    ImGui::EndTabItem();
                        }
                            if (ImGui::BeginTabItem("Aimbot")) {
                                    ImGui::Checkbox("Enable Aimbot", &aimbot_enabled);
                                            ImGui::SliderFloat("FOV", &aim_fov, 1.0f, 20.0f);
                                                    ImGui::EndTabItem();
                                                        }
                                                            ImGui::EndTabBar();
                                                            }
                                                            ImGui::End();

                                                            ImGui::Render();
                                                            glViewport(0, 0, (int)ImGui::GetIO().DisplaySize.x, (int)ImGui::GetIO().DisplaySize.y);
                                                            glClearColor(0, 0, 0, 0);
                                                            glClear(GL_COLOR_BUFFER_BIT);
                                                            ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
                                                            eglSwapBuffers(g_EglDisplay, g_EglSurface);
                                                            }

                                                            extern "C" JNIEXPORT void JNICALL
                                                            Java_com_bbb_gaming_MainActivity_initMenu(JNIEnv *env, jobject thiz, jobject surface) {
                                                            g_Window = ANativeWindow_fromSurface(env, surface);

                                                            g_EglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
                                                            eglInitialize(g_EglDisplay, nullptr, nullptr);

                                                            EGLint configAttribs[] = {
                                                                EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
                                                                    EGL_RED_SIZE, 8, EGL_GREEN_SIZE, 8, EGL_BLUE_SIZE, 8, EGL_ALPHA_SIZE, 8,
                                                                        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT,
                                                                            EGL_NONE
                                                                            };
                                                                            EGLConfig eglConfig; EGLint numConfigs;
                                                                            eglChooseConfig(g_EglDisplay, configAttribs, &eglConfig, 1, &numConfigs);

                                                                            EGLint contextAttribs[] = { EGL_CONTEXT_CLIENT_VERSION, 3, EGL_NONE };
                                                                            g_EglContext = eglCreateContext(g_EglDisplay, eglConfig, EGL_NO_CONTEXT, contextAttribs);
                                                                            g_EglSurface = eglCreateWindowSurface(g_EglDisplay, eglConfig, g_Window, nullptr);

                                                                            eglMakeCurrent(g_EglDisplay, g_EglSurface, g_EglSurface, g_EglContext);

                                                                            InitImGui(ANativeWindow_getWidth(g_Window), ANativeWindow_getHeight(g_Window));
                                                                            LOGI("Menu Initialized");
                                                                            }

                                                                            extern "C" JNIEXPORT void JNICALL
                                                                            Java_com_bbb_gaming_MainActivity_drawMenu(JNIEnv *env, jobject thiz) {
                                                                            if (g_EglDisplay != EGL_NO_DISPLAY) {
                                                                            RenderMenu();
                                                                            }
                                                                            }