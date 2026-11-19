cmake_minimum_required(VERSION 3.22.1)
project("abk")
--- إعدادات ImGui ---
set(IMGUI_DIR ${CMAKE_SOURCE_DIR}/imgui)
add_library(imgui STATIC
${IMGUI_DIR}/imgui.cpp
${IMGUI_DIR}/imgui_draw.cpp
${IMGUI_DIR}/imgui_tables.cpp
${IMGUI_DIR}/imgui_widgets.cpp
${IMGUI_DIR}/backends/imgui_impl_opengl3.cpp
${IMGUI_DIR}/backends/imgui_impl_android.cpp
)
target_include_directories(imgui PUBLIC ${IMGUI_DIR} ${IMGUI_DIR}/backends)
--- إعدادات مكتبتنا ---
add_library(abk SHARED menu.cpp)
target_link_libraries(abk imgui android log GLESv3 EGL)