package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public final class ServidorLeitorCodigoBarras {
    private static final int PORTA_PADRAO = 8765;
    private static ServerSocket servidor;
    private static volatile boolean emExecucao = false;
    private static Consumer<String> ouvinteAtivo;

    private ServidorLeitorCodigoBarras() {
    }

    public static synchronized String iniciarOuAtualizarOuvinte(Consumer<String> aoLerCodigo) throws IOException {
        ouvinteAtivo = aoLerCodigo;
        if (servidor == null || servidor.isClosed()) {
            servidor = new ServerSocket(PORTA_PADRAO);
            emExecucao = true;
            Thread threadServidor = new Thread(ServidorLeitorCodigoBarras::loopAceitacao, "ServidorLeitorCodigoBarras");
            threadServidor.setDaemon(true);
            threadServidor.start();
        }
        return "http://" + obterIpLocal() + ":" + PORTA_PADRAO + "/leitor";
    }

    public static synchronized void parar() {
        emExecucao = false;
        if (servidor != null) {
            try {
                servidor.close();
            } catch (IOException ignored) {
            }
            servidor = null;
        }
    }

    private static void loopAceitacao() {
        while (emExecucao && servidor != null && !servidor.isClosed()) {
            try {
                final Socket cliente = servidor.accept();
                Thread t = new Thread(() -> tratarConexao(cliente));
                t.setDaemon(true);
                t.start();
            } catch (IOException ignored) {
                if (!emExecucao) {
                    break;
                }
            }
        }
    }

    private static void tratarConexao(Socket socket) {
        try (Socket s = socket;
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                OutputStream out = s.getOutputStream()) {
            String linhaRequisicao = in.readLine();
            if (linhaRequisicao == null || linhaRequisicao.isEmpty()) {
                return;
            }
            String[] partes = linhaRequisicao.split(" ");
            String caminhoCompleto = partes.length > 1 ? partes[1] : "/leitor";
            if (caminhoCompleto.startsWith("/scan")) {
                receberLeitura(caminhoCompleto, out);
            } else {
                servirPaginaMobile(out);
            }
        } catch (Exception ignored) {
        }
    }

    // Versão 1 sem QR-code

    /*
     * private static void servirPaginaMobile(OutputStream out) throws IOException {
     * String html = "<!doctype html><html lang='pt'><head><meta charset='utf-8'>"
     * + "<meta name='viewport' content='width=device-width,initial-scale=1'>"
     * + "<title>Leitor Kumbeza</title>"
     * +
     * "<style>body{background:#12151C;color:#E8ECF4;font-family:sans-serif;padding:20px;text-align:center}"
     * +
     * ".card{background:#1B2030;border:1px solid #2A3040;border-radius:14px;padding:18px;max-width:420px;margin:0 auto}"
     * +
     * "input,button{width:100%;padding:12px;border-radius:8px;border:1px solid #2E3648;font-size:16px;box-sizing:border-box;margin-top:10px}"
     * +
     * "input{background:#151A26;color:#E8ECF4}button{background:#F5B638;color:#12151C;font-weight:700;border:none;cursor:pointer}"
     * +
     * "video{width:100%;border-radius:10px;margin-top:10px;background:#000}</style></head><body>"
     * + "<div class='card'><h2>Leitor de Código de Barras</h2>"
     * +
     * "<p style='color:#A3ADC2;font-size:13px'>Aponte a câmera ou digite/leia o código abaixo para enviar à Venda ativa no computador.</p>"
     * + "<video id='v' autoplay playsinline muted></video>"
     * +
     * "<form onsubmit='enviar(event)'><input id='c' placeholder='Código de barras ou ID...' autofocus>"
     * + "<button type='submit'>Enviar para o Caixa</button></form>"
     * +
     * "<p id='msg' style='color:#4ADE80;font-size:13px;min-height:20px'></p></div>"
     * + "<script>"
     * +
     * "async function enviarCodigo(cod){if(!cod)return;await fetch('/scan?codigo='+encodeURIComponent(cod));"
     * +
     * "document.getElementById('msg').innerText='Enviado: '+cod;document.getElementById('c').value='';}"
     * +
     * "function enviar(e){e.preventDefault();enviarCodigo(document.getElementById('c').value.trim());}"
     * + "if('BarcodeDetector' in window && navigator.mediaDevices){"
     * +
     * "navigator.mediaDevices.getUserMedia({video:{facingMode:'environment'}}).then(s=>{const v=document.getElementById('v');v.srcObject=s;"
     * +
     * "const bd=new BarcodeDetector();setInterval(async()=>{try{const codes=await bd.detect(v);if(codes.length>0){enviarCodigo(codes[0].rawValue);}}catch(e){}},600);}).catch(()=>{});}"
     * + "</script></body></html>";
     * byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
     * String cabecalho = "HTTP/1.1 200 OK\r\n"
     * + "Content-Type: text/html; charset=utf-8\r\n"
     * + "Content-Length: " + bytes.length + "\r\n"
     * + "Connection: close\r\n\r\n";
     * out.write(cabecalho.getBytes(StandardCharsets.UTF_8));
     * out.write(bytes);
     * out.flush();
     * }
     */

    // Versão 2 com QR-code mas sem imformação de suporte ao navegador

    /*
     * private static void servirPaginaMobile(OutputStream out) throws IOException {
     * String html = "<!doctype html><html lang='pt'><head><meta charset='utf-8'>"
     * + "<meta name='viewport' content='width=device-width,initial-scale=1'>"
     * + "<title>Leitor Kumbeza</title>"
     * +
     * "<style>body{background:#12151C;color:#E8ECF4;font-family:sans-serif;padding:20px;text-align:center}"
     * +
     * ".card{background:#1B2030;border:1px solid #2A3040;border-radius:14px;padding:18px;max-width:420px;margin:0 auto}"
     * +
     * "input,button{width:100%;padding:12px;border-radius:8px;border:1px solid #2E3648;font-size:16px;box-sizing:border-box;margin-top:10px}"
     * +
     * "input{background:#151A26;color:#E8ECF4}button{background:#F5B638;color:#12151C;font-weight:700;border:none;cursor:pointer}"
     * +
     * "video{width:100%;border-radius:10px;margin-top:10px;background:#000}</style></head><body>"
     * + "<div class='card'><h2>Leitor de Código de Barras</h2>"
     * +
     * "<p style='color:#A3ADC2;font-size:13px'>Aponte a câmera ou digite/leia o código abaixo para enviar à Venda ativa.</p>"
     * + "<video id='v' autoplay playsinline muted></video>"
     * +
     * "<form onsubmit='enviar(event)'><input id='c' placeholder='Código de barras ou ID...' autofocus>"
     * + "<button type='submit'>Enviar para o Caixa</button></form>"
     * +
     * "<p id='msg' style='color:#4ADE80;font-size:13px;min-height:20px'></p></div>"
     * + "<script>"
     * +
     * "async function enviarCodigo(cod){if(!cod)return;await fetch('/scan?codigo='+encodeURIComponent(cod));"
     * +
     * "document.getElementById('msg').innerText='Enviado: '+cod;document.getElementById('c').value='';}"
     * +
     * "function enviar(e){e.preventDefault();enviarCodigo(document.getElementById('c').value.trim());}"
     * + "if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {"
     * +
     * "  alert('Erro: O navegador bloqueou a câmera porque a ligação não é segura (HTTP). Configure a flag no Chrome conforme instruído.');"
     * + "} else if (!('BarcodeDetector' in window)) {"
     * +
     * "  alert('Aviso: Este navegador não suporta leitura direta pela câmera. Use o Google Chrome num Android ou Windows.');"
     * + "} else {"
     * +
     * "  navigator.mediaDevices.getUserMedia({video:{facingMode:'environment'}}).then(s=>{"
     * + "    const v=document.getElementById('v');v.srcObject=s;"
     * + "    const bd=new BarcodeDetector();"
     * + "    setInterval(async()=>{"
     * +
     * "      try{const codes=await bd.detect(v);if(codes.length>0){enviarCodigo(codes[0].rawValue);}}catch(e){}"
     * + "    },600);"
     * + "  }).catch(err=>{"
     * +
     * "    alert('Erro ao ligar a câmera: ' + err.message + '\\nVerifique se deu permissão ao navegador.');"
     * + "  });"
     * + "}"
     * + "</script></body></html>";
     * byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
     * String cabecalho = "HTTP/1.1 200 OK\r\n"
     * + "Content-Type: text/html; charset=utf-8\r\n"
     * + "Content-Length: " + bytes.length + "\r\n"
     * + "Connection: close\r\n\r\n";
     * out.write(cabecalho.getBytes(StandardCharsets.UTF_8));
     * out.write(bytes);
     * out.flush();
     * }
     */

    private static void servirPaginaMobile(OutputStream out) throws IOException {
        String html = "<!doctype html><html lang='pt'><head><meta charset='utf-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1'>"
                + "<title>Leitor Kumbeza</title>"
                + "<style>body{background:#12151C;color:#E8ECF4;font-family:sans-serif;padding:20px;text-align:center}"
                + ".card{background:#1B2030;border:1px solid #2A3040;border-radius:14px;padding:18px;max-width:420px;margin:0 auto}"
                + "input,button{width:100%;padding:12px;border-radius:8px;border:1px solid #2E3648;font-size:16px;box-sizing:border-box;margin-top:10px}"
                + "input{background:#151A26;color:#E8ECF4}button{background:#F5B638;color:#12151C;font-weight:700;border:none;cursor:pointer}"
                + "video{width:100%;border-radius:10px;margin-top:10px;background:#000}"
                + ".modal{display:none;position:fixed;inset:0;background:rgba(0,0,0,0.85);z-index:1000;align-items:center;justify-content:center;padding:20px;}"
                + ".modal-content{background:#1B2030;padding:20px;border-radius:14px;text-align:left;max-width:400px;width:100%;border:1px solid #2A3040;box-sizing:border-box;}"
                + ".code-block{background:#12151C;padding:12px;border-radius:8px;margin:8px 0;font-family:monospace;word-break:break-all;color:#4ADE80;user-select:all;cursor:text;}"
                + "</style></head><body>"

                + "<div class='card'><h2>Leitor de Código de Barras</h2>"
                + "<p style='color:#A3ADC2;font-size:13px'>Aponte a câmera ou digite/leia o código abaixo para enviar à Venda ativa.</p>"
                + "<video id='v' autoplay playsinline muted></video>"
                + "<form onsubmit='enviar(event)'><input id='c' placeholder='Código de barras ou ID...' autofocus>"
                + "<button type='submit'>Enviar para o Caixa</button></form>"
                + "<p id='msg' style='color:#4ADE80;font-size:13px;min-height:20px'></p></div>"

                + "<div id='modalErro' class='modal'>"
                + "<div class='modal-content'>"
                + "<h3 style='margin-top:0'>Câmera Bloqueada</h3>"
                + "<p style='font-size:14px;color:#A3ADC2'>O navegador bloqueou a câmera. Para liberar, siga estes passos (toque nos blocos verdes para copiar):</p>"
                + "<ol style='padding-left:20px;font-size:14px;margin-bottom:20px;'>"
                + "<li>Copie e cole na barra do navegador:<br><div class='code-block'>chrome://flags/#unsafely-treat-insecure-origin-as-secure</div></li>"
                + "<li>Na caixa de texto da página que abrir, cole o seu IP exato:<br><div class='code-block' id='ipAtual'></div></li>"
                + "<li>Mude o botão ao lado de <i>Disabled</i> para <b>Enabled</b>.</li>"
                + "<li>Clique no botão azul <b>Relaunch</b> no fundo da tela.</li>"
                + "</ol>"
                + "<button onclick=\"document.getElementById('modalErro').style.display='none'\">OK, entendi</button>"
                + "</div></div>"

                + "<script>"
                + "document.getElementById('ipAtual').innerText = window.location.origin;"
                + "let ultimoCodigo = ''; let tempoUltimo = 0;"
                + "async function enviarCodigo(cod){if(!cod)return;await fetch('/scan?codigo='+encodeURIComponent(cod));"
                + "document.getElementById('msg').innerText='Enviado: '+cod;document.getElementById('c').value='';}"
                + "function enviar(e){e.preventDefault();enviarCodigo(document.getElementById('c').value.trim());}"
                + "if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {"
                + "  document.getElementById('modalErro').style.display='flex';"
                + "} else if (!('BarcodeDetector' in window)) {"
                + "  alert('Aviso: Este navegador não suporta leitura direta pela câmera. Use o Google Chrome.');"
                + "} else {"
                + "  navigator.mediaDevices.getUserMedia({video:{facingMode:'environment'}}).then(s=>{"
                + "    const v=document.getElementById('v');v.srcObject=s;"
                + "    const bd=new BarcodeDetector();"
                + "    setInterval(async()=>{"
                + "      try{"
                + "        const codes=await bd.detect(v);"
                + "        if(codes.length>0){"
                + "          let cod = codes[0].rawValue;"
                + "          let agora = Date.now();"
                + "          if(cod !== ultimoCodigo || (agora - tempoUltimo) > 4000){"
                + "            ultimoCodigo = cod; tempoUltimo = agora; enviarCodigo(cod);"
                + "          }"
                + "        }"
                + "      }catch(e){}"
                + "    },600);"
                + "  }).catch(err=>{"
                + "    document.getElementById('modalErro').style.display='flex';"
                + "  });"
                + "}"
                + "</script></body></html>";

        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        String cabecalho = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html; charset=utf-8\r\n"
                + "Content-Length: " + bytes.length + "\r\n"
                + "Connection: close\r\n\r\n";
        out.write(cabecalho.getBytes(StandardCharsets.UTF_8));
        out.write(bytes);
        out.flush();
    }

    private static void receberLeitura(String caminhoCompleto, OutputStream out) throws IOException {
        int idxInterrogacao = caminhoCompleto.indexOf('?');
        String query = idxInterrogacao >= 0 ? caminhoCompleto.substring(idxInterrogacao + 1) : null;
        String codigo = null;
        if (query != null) {
            for (String par : query.split("&")) {
                if (par.startsWith("codigo=")) {
                    codigo = URLDecoder.decode(par.substring("codigo=".length()), StandardCharsets.UTF_8.name()).trim();
                }
            }
        }
        if (codigo != null && !codigo.isEmpty() && ouvinteAtivo != null) {
            final String codFinal = codigo;
            Consumer<String> cb = ouvinteAtivo;
            SwingUtilities.invokeLater(() -> cb.accept(codFinal));
        }
        byte[] ok = "OK".getBytes(StandardCharsets.UTF_8);
        String cabecalho = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/plain; charset=utf-8\r\n"
                + "Content-Length: " + ok.length + "\r\n"
                + "Connection: close\r\n\r\n";
        out.write(cabecalho.getBytes(StandardCharsets.UTF_8));
        out.write(ok);
        out.flush();
    }

    /*
     * private static String obterIpLocal() {
     * try {
     * Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
     * while (nets.hasMoreElements()) {
     * NetworkInterface ni = nets.nextElement();
     * if (!ni.isUp() || ni.isLoopback()) continue;
     * Enumeration<InetAddress> addrs = ni.getInetAddresses();
     * while (addrs.hasMoreElements()) {
     * InetAddress addr = addrs.nextElement();
     * if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
     * return addr.getHostAddress();
     * }
     * }
     * }
     * } catch (Exception ignored) {
     * }
     * return "127.0.0.1";
     * }
     */

    private static String obterIpLocal() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface ni = nets.nextElement();

                // Ignorar placas desligadas ou de loopback
                if (!ni.isUp() || ni.isLoopback())
                    continue;

                // Ignorar placas virtuais comuns (VirtualBox, VMware, WSL, VPNs)
                String nome = ni.getDisplayName().toLowerCase();
                if (nome.contains("virtual") || nome.contains("vmware")
                        || nome.contains("wsl") || nome.contains("vethernet")) {
                    continue;
                }

                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    // Capturar apenas o endereço IPv4 real
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "127.0.0.1";
    }
}
