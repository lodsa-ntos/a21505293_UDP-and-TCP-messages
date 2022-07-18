package Redes.p2p;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorP2P extends Thread {

    // Vector que controla as conexões por meio de threads.
    private static Vector CLIENTES;

    // socket para conexao dos clientes
    private final Socket conexao;

    // contém o nome dos clientes
    String nomeCliente;

    // lista que armazena nome de CLIENTES
    private static final List LISTA_DE_NOMES = new ArrayList();

    // construtor que recebe o socket dos cliente
    public ServidorP2P(Socket socket) {
        this.conexao = socket;
    }

    // testa se os nomes são iguais, se for retorna true
    public boolean armazenar(String newName) {
        // System.out.println(LISTA_DE_NOMES);
        for (int i = 0; i < LISTA_DE_NOMES.size(); i++) {
            if (LISTA_DE_NOMES.get(i).equals(newName)) {
                return true;
            }
        }
        // adiciona na lista apenas se não existir
        LISTA_DE_NOMES.add(newName);
        return false;
    }

    // apagar da lista os CLIENTES que já deixaram o chat
    public void apagar(String oldName) {
        for (int i = 0; i < LISTA_DE_NOMES.size(); i++) {
            if (LISTA_DE_NOMES.get(i).equals(oldName)) {
                LISTA_DE_NOMES.remove(oldName);
            }
        }
    }

    public static void main(String args[]) {
        // instancia o vetor de CLIENTES conetados
        CLIENTES = new Vector();
        int porta = 9996;
        try {
            // cria um socket que fica a espera na porta 9996.
            ServerSocket server = new ServerSocket(9996);
            System.out.println("A aguardar a conexao na porta " + porta);

            // Loop principal.
            while (true) {
                // aguarda que um cliente se conecte.
                // A execução do servidor fica bloqueada na chamada do método accept da
                // classe ServerSocket até que um cliente se conecte ao servidor.
                // O próprio método desbloqueia e retorna com um objeto da classe Socket
                Socket conexao = server.accept();
                System.out.println("Conexao estabelecida no IP: " + conexao.getInetAddress().getHostAddress());
                // cria uma nova thread para tratar dessa conexão
                Thread t = new ServidorP2P(conexao);
                t.start();
                // voltando ao loop, e fica a espera que mais alguém se conecte
            }
        } catch (IOException e) {
            // caso ocorra alguma excessão, vai mostrar qual foi.
            System.out.println("IOException: " + e);
        }
    }

    @Override
    // Executa a thread
    public void run() {

        try {
            // objetos que permitem controlar fluxo da comunicação que vem do cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));

            PrintStream saida = new PrintStream(this.conexao.getOutputStream());

            // BufferedWriter out = new BufferedWriter(new
            // OutputStreamWriter(this.conexao.getOutputStream()));
            PrintWriter out = new PrintWriter(this.conexao.getOutputStream(), true);
            // recebe o nome do cliente
            this.nomeCliente = entrada.readLine();
            // metodo que testa se os nomes são iguais
            if (armazenar(this.nomeCliente)) {
                saida.println("Este nome já existe! Tente novamente mas com outro nome.");
                CLIENTES.add(saida);
                // fecha a conexao com esse cliente
                this.conexao.close();
                return;
            } else {
                // mostra o nome do cliente conetado ao servidor
                System.out.println(this.nomeCliente + " : Conetado ao Servidor!");
            }
            // igual a null termina a execução
            if (this.nomeCliente == null) {
                return;
            }
            // adiciona os dados de saida do cliente no objeto CLIENTES
            CLIENTES.add(saida);
            // recebe a mensagem do cliente
            String msg = entrada.readLine();

            // Verificar se a linha é null (conexão terminada)
            // Se não for nula, mostra a troca de mensagens entre os CLIENTES
            if (msg != null && !(msg.trim().equals(""))) {
                // reenvia a linha para todos os CLIENTES conetados
                System.out.println(saida + " escreveu: " + msg);
                // espera por uma nova linha.
                // msg = entrada.readLine();
                System.out.println("ECHO");
                out.println("Recebi a msg");
                System.out.println("ECHO2");
            }
            // se o cliente enviar uma linha em branco, mostra a saida no servidor
            // System.out.println(this.nomeCliente + " saiu do Chat!");
            //// se cliente enviar uma linha em branco, servidor uma envia
            // mensagem de saida do chat aos CLIENTES conectados
            // sendToAll(saida, " saiu", " do Chat!");
            // apaga o nome da lista
            // apagar(this.nomeCliente);
            // apaga os atributos ligados do cliente que se desconectou
            // CLIENTES.remove(saida);
            // fecha a conexao com o cliente que desconectou
            // this.conexao.close();
        } catch (IOException e) {
            // Caso ocorra alguma excessão, vai mostrar qual foi
            System.out.println("Falha na conexao... .. ." + " IOException: " + e);
        }
    }

    // enviar uma mensagem para todos, menos para o próprio
    public void sendToAll(PrintStream saida, String acao, String msg) throws IOException {
        Enumeration e = CLIENTES.elements();

        while (e.hasMoreElements()) {
            // obtém o fluxo de saída de um dos CLIENTES
            PrintStream chat = (PrintStream) e.nextElement();
            // envia para todos, menos para o próprio cliente que enviou
            if (chat != saida) {
                chat.println(this.nomeCliente + acao + msg);
            }
        }
    }
}
