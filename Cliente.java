package Redes;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Cliente extends Thread {

    // Socket que controla a recepção de mensagens do cliente
    private final Socket conexao;

    // construtor que recebe o socket do cliente
    public Cliente(Socket socket) {
        this.conexao = socket;
    }

    public static void main(String args[]) {

        ArrayList<String> clientesConetados = new ArrayList<String>();

        int porta = 9996;
        String servidor = "172.19.50.137";

        System.out.println("A conectar servidor com o IP: " + servidor + ":" + porta + " ...");

        try {
            // Instancia do atributo que conecta ao tipo Socket,
            // conecta ao IP do Servidor, na Porta 9996
            Socket socket = new Socket(servidor, porta);
            System.out.println("O cliente se conectou ao servidor!");

            // Instancia do atributo saida, que obtem os objetos que permitem
            // controlar o fluxo da comunicação
            PrintStream saida = new PrintStream(socket.getOutputStream());
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Introduza o seu nome: ");
            String meuNome = teclado.readLine();
            int i;

            // envia o nome inserido para o servidor
            saida.println(meuNome.toUpperCase());

            System.out.println("\n\n::....Chat....::");
            // instancia a thread para o ip e a porta conectados e depois inicia nela
            Thread thread = new Cliente(socket);
            thread.start();

            // Cria a variavel msg responsavel por enviar a mensagem para o servidor
            String msg;

            while (true) {
                // cria uma linha para a troca de mensagem e armazena na variavel msg
                System.out.print("Mensagem > ");
                msg = teclado.readLine();
                // envia a mensagem para o servidor
                saida.println(msg);
            }
        } catch (IOException e) {
            // Caso ocorra alguma excessão, vai mostrar qual foi
            System.out.println("Falha na conexao... .. ." + " IOException: " + e);
        }
    }

    @Override
    // Executa a thread
    public void run() {

        try {
            // recebe as mensagens de outro cliente através do servidor
            BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));

            // variavel para a mensagem
            String msg;

            while (true) {
                // pega o que o servidor enviou
                msg = entrada.readLine();
                // se a mensagem tiver dados, passa pelo if,
                // caso contrario cai no break e termina a conexao
                if (msg == null) {
                    System.out.println("Fica fixe..Bzei!");
                    System.out.println("\nConexão terminada!");
                    System.exit(0);
                }
                System.out.println();
                // imprime a mensagem recebida
                System.out.println(msg);
                // cria uma linha para dar a resposta
                System.out.print("Responder > ");
            }
        } catch (IOException e) {
            // Caso ocorra alguma excessão, vai mostrar qual foi
            System.out.println("Ocorreu uma Falha... .. ." + " IOException: " + e);
        }
    }
}