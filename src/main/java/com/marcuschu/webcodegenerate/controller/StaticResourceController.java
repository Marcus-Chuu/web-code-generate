package com.marcuschu.webcodegenerate.controller;


import cn.hutool.core.io.FileUtil;
import com.marcuschu.webcodegenerate.constant.AppConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.io.File;
import java.util.List;

/**
 * 静态资源访问
 */
@RestController
@RequestMapping("/static")
public class StaticResourceController {

    /**
     * 已部署应用目录（deployApp 按 deployKey 输出到 code_deploy）
     */
    private static final String DEPLOY_ROOT_DIR = AppConstant.CODE_DEPLOY_ROOT_DIR;

    /**
     * 应用生成根目录（开发中的源码预览，按 codeGenType_appId 输出到 code_output）
     */
    private static final String PREVIEW_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;

    /**
     * 资源查找顺序：先匹配已部署的应用(deployKey)，再匹配生成中的源码(codeGenType_appId)
     */
    private static final List<String> RESOURCE_ROOTS = List.of(DEPLOY_ROOT_DIR, PREVIEW_ROOT_DIR);

    /**
     * 可视化编辑模式开关参数
     */
    private static final String EDITOR_PARAM = "editor";

    /**
     * 注入到生成页面中的“元素选取”脚本（仅当 editor=1 时注入 HTML）。
     * 脚本负责悬浮高亮、点击锁定并高亮选中元素，通过 window.parent.postMessage 将元素信息回传给主站。
     */
    private static final String EDITOR_SCRIPT = """
            <script>
            (function () {
              if (window.__veInstalled) return;
              window.__veInstalled = true;

              var hover = null;
              var selected = null;

              function outline(el, color) {
                el.style.outline = '2px solid ' + color;
                el.style.outlineOffset = '1px';
              }
              function clearHover() {
                if (hover) { hover.style.outline = ''; hover = null; }
              }
              function collapse(s) {
                var out = '', lastSpace = false;
                for (var i = 0; i < s.length; i++) {
                  var space = s.charCodeAt(i) <= 32;
                  if (space) { if (!lastSpace && out) out += ' '; }
                  else out += s[i];
                  lastSpace = space;
                }
                return out.slice(0, 200);
              }
              function describe(el) {
                return {
                  tag: el.tagName ? el.tagName.toLowerCase() : '',
                  id: el.id || '',
                  className: typeof el.className === 'string' ? el.className : '',
                  text: collapse(el.innerText || el.textContent || ''),
                  src: el.getAttribute ? (el.getAttribute('src') || el.getAttribute('href') || '') : '',
                  alt: el.getAttribute ? (el.getAttribute('alt') || '') : '',
                  placeholder: el.getAttribute ? (el.getAttribute('placeholder') || '') : ''
                };
              }
              function post(type, payload) {
                try { window.parent.postMessage({ source: 'visual-editor', type: type, payload: payload }, '*'); } catch (e) {}
              }

              document.addEventListener('mouseover', function (e) {
                var t = e.target;
                if (!t || t.nodeType !== 1 || t === selected) return;
                clearHover();
                hover = t;
                outline(t, '#4f9cff');
              }, true);

              document.addEventListener('mouseout', function (e) {
                if (hover === e.target) clearHover();
              }, true);

              document.addEventListener('click', function (e) {
                e.preventDefault();
                e.stopPropagation();
                var t = e.target;
                if (!t || t.nodeType !== 1) return;
                if (selected) selected.style.outline = '';
                selected = t;
                outline(t, '#e5484d');
                post('select', describe(t));
              }, true);

              window.addEventListener('message', function (e) {
                var msg = e.data;
                if (!msg || msg.source !== 'visual-editor') return;
                if (msg.type === 'clear') {
                  if (selected) selected.style.outline = '';
                  selected = null;
                  clearHover();
                }
              });

              document.addEventListener('mouseleave', clearHover);
            })();
            </script>
            """;

    /**
     * 提供静态资源访问，支持目录重定向
     * 访问格式：http://localhost:8123/api/static/{deployKey}[/{fileName}]
     * 当附带 editor=1 且目标为 HTML 时，注入元素选取脚本用于可视化编辑。
     */
    @GetMapping("/{deployKey}/**")
    public ResponseEntity<?> serveStaticResource(
            @PathVariable String deployKey,
            HttpServletRequest request) {
        try {
            // 获取资源路径
            String resourcePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            resourcePath = resourcePath.substring(("/static/" + deployKey).length());
            // 如果是目录访问（不带斜杠），重定向到带斜杠的URL
            if (resourcePath.isEmpty()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", request.getRequestURI() + "/");
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            }
            // 默认返回 index.html
            if (resourcePath.equals("/")) {
                resourcePath = "/index.html";
            }
            // 依次从部署目录、生成目录中查找文件
            File file = resolveFile(deployKey, resourcePath);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            String contentType = getContentTypeWithCharset(file.getAbsolutePath());
            // 可视化编辑模式: 对 HTML 页面注入元素选取脚本
            if (contentType.startsWith("text/html") && "1".equals(request.getParameter(EDITOR_PARAM))) {
                String html = FileUtil.readUtf8String(file);
                return ResponseEntity.ok()
                        .header("Content-Type", contentType)
                        .body(injectEditorScript(html));
            }
            // 返回文件资源
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 按 deployKey 依次在 code_deploy、code_output 目录下查找资源文件
     * @param deployKey 部署 key（或生成中项目的 codeGenType_appId）
     * @param resourcePath 资源相对路径（以 / 开头）
     * @return 命中的文件，未找到返回 null
     */
    private File resolveFile(String deployKey, String resourcePath) {
        for (String root : RESOURCE_ROOTS) {
            File candidate = new File(root, deployKey + resourcePath);
            if (candidate.exists()) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * 将元素选取脚本注入到 HTML 中（优先 </body> 前，其次 </html> 前，否则追加到末尾）
     */
    private String injectEditorScript(String html) {
        String lower = html.toLowerCase();
        int bodyEnd = lower.lastIndexOf("</body>");
        if (bodyEnd >= 0) {
            return html.substring(0, bodyEnd) + EDITOR_SCRIPT + html.substring(bodyEnd);
        }
        int htmlEnd = lower.lastIndexOf("</html>");
        if (htmlEnd >= 0) {
            return html.substring(0, htmlEnd) + EDITOR_SCRIPT + html.substring(htmlEnd);
        }
        return html + EDITOR_SCRIPT;
    }

    /**
     * 根据文件扩展名返回带字符编码的 Content-Type
     */
    private String getContentTypeWithCharset(String filePath) {
        if (filePath.endsWith(".html")) return "text/html; charset=UTF-8";
        if (filePath.endsWith(".css")) return "text/css; charset=UTF-8";
        if (filePath.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg")) return "image/jpeg";
        return "application/octet-stream";
    }
}
