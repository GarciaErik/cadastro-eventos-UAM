package model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

// Enum for categories
enum Categoria {
    FESTA, ESPORTIVO, SHOW, CULTURAL, OUTROS
}

class Usuario {
    /**
     * Classe representando um usuário do sistema.
     * Atributos mínimos: nome, email, telefone. Adicionei idade para completude.
     */
    private String nome;
    private String email;
    private String telefone;
    private int idade;
    private List<Integer> participacoes;  // Lista de IDs de eventos confirmados

    public Usuario(String nome, String email, String telefone, int idade) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.idade = idade;
        this.participacoes = new ArrayList<>();
    }

    public void adicionarParticipacao(int eventoId) {
        if (!participacoes.contains(eventoId)) {
            participacoes.add(eventoId);
        }
    }

    public void removerParticipacao(int eventoId) {
        participacoes.remove(Integer.valueOf(eventoId));
    }

    public List<Evento> listarParticipacoes(List<Evento> eventos) {
        List<Evento> participando = new ArrayList<>();
        for (Evento e : eventos) {
            if (participacoes.contains(e.getId())) {
                participando.add(e);
            }
        }
        participando.sort(Comparator.comparing(Evento::getHorario));
        return participando;
    }

    // Getters
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public int getIdade() { return idade; }
    public List<Integer> getParticipacoes() { return new ArrayList<>(participacoes); }
}

class Evento {
    /**
     * Classe representando um evento.
     * Atributos obrigatórios: nome, endereco, categoria, horario (LocalDateTime), descricao.
     * Adicionei id único e duracao (em horas) para verificar se está ocorrendo.
     */
    private int id;
    private String nome;
    private String endereco;
    private Categoria categoria;
    private LocalDateTime horario;
    private String descricao;
    private int duracao;  // Duração em horas

    public Evento(String nome, String endereco, Categoria categoria, LocalDateTime horario, String descricao, int duracao) {
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
        this.duracao = duracao;
    }

    public boolean estaOcorrendo() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime fim = horario.plusHours(duracao);
        return !agora.isBefore(horario) && agora.isBefore(fim);
    }

    public boolean jaOcorrreu() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime fim = horario.plusHours(duracao);
        return agora.isAfter(fim) || agora.isEqual(fim);
    }

    // toString for serialization
    @Override
    public String toString() {
        return id + "|" + nome + "|" + endereco + "|" + categoria + "|" + horario.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "|" + descricao.replace("|", "\\|") + "|" + duracao;
    }

    // fromString for deserialization
    public static Evento fromString(String linha, int id) {
        String[] partes = linha.split("\\|", -1);
        if (partes.length != 7) throw new IllegalArgumentException("Formato inválido");
        String nome = partes[1].replace("\\|", "|");
        String endereco = partes[2].replace("\\|", "|");
        Categoria cat = Categoria.valueOf(partes[3]);
        LocalDateTime hor = LocalDateTime.parse(partes[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String desc = partes[5].replace("\\|", "|");
        int dur = Integer.parseInt(partes[6]);
        Evento e = new Evento(nome, endereco, cat, hor, desc, dur);
        e.id = id;
        return e;
    }

    // Getters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public Categoria getCategoria() { return categoria; }
    public LocalDateTime getHorario() { return horario; }
    public String getDescricao() { return descricao; }
    public int getDuracao() { return duracao; }

    public String toStringDisplay() {
        return "ID: " + id + " | " + nome + " | " + endereco + " | " + categoria + " | Horário: " + horario.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " | Descrição: " + descricao.substring(0, Math.min(50, descricao.length())) + "...";
    }
}

class SistemaEventos {
    /**
     * Classe principal do sistema, gerenciando usuários, eventos e persistência.
     * Implementa menu console para interações.
     */
    private List<Usuario> usuarios;
    private List<Evento> eventos;
    private Usuario usuarioAtual;
    private String arquivoEventos = "events.data";
    private int nextId;

    public SistemaEventos() {
        usuarios = new ArrayList<>();
        eventos = new ArrayList<>();
        usuarioAtual = null;
        carregarEventos();
        nextId = eventos.stream().mapToInt(Evento::getId).max().orElse(0) + 1;
    }

    public void cadastrarUsuario() {
        System.out.println("\n=== Cadastro de Usuário ===");
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Telefone: ");
        String telefone = sc.nextLine();
        System.out.print("Idade (opcional, 0): ");
        int idade = 0;
        try {
            idade = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            // Ignora, usa 0
        }
        Usuario usuario = new Usuario(nome, email, telefone, idade);
        usuarios.add(usuario);
        usuarioAtual = usuario;
        System.out.println("Usuário " + nome + " cadastrado com sucesso!");
    }

    public boolean selecionarUsuario() {
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado. Cadastre um primeiro.");
            return false;
        }
        System.out.println("\n=== Selecionar Usuário ===");
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            System.out.println((i+1) + ". " + u.getNome() + " (" + u.getEmail() + ")");
        }
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        try {
            int idx = Integer.parseInt(sc.nextLine()) - 1;
            if (idx >= 0 && idx < usuarios.size()) {
                usuarioAtual = usuarios.get(idx);
                System.out.println("Usuário " + usuarioAtual.getNome() + " selecionado.");
                return true;
            } else {
                System.out.println("Seleção inválida.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Seleção inválida.");
            return false;
        }
    }

    public void cadastrarEvento() {
        System.out.println("\n=== Cadastro de Evento ===");
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        System.out.print("Nome do evento: ");
        String nome = sc.nextLine();
        System.out.print("Endereço: ");
        String endereco = sc.nextLine();
        System.out.println("Categorias disponíveis: " + Arrays.toString(Categoria.values()));
        System.out.print("Categoria: ");
        String catStr = sc.nextLine().toUpperCase();
        Categoria categoria;
        try {
            categoria = Categoria.valueOf(catStr);
        } catch (IllegalArgumentException e) {
            categoria = Categoria.OUTROS;
        }
        System.out.print("Horário (yyyy-MM-dd HH:mm): ");
        String horarioStr = sc.nextLine();
        LocalDateTime horario;
        try {
            horario = LocalDateTime.parse(horarioStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            System.out.println("Formato de data inválido. Use yyyy-MM-dd HH:mm");
            return;
        }
        System.out.print("Descrição: ");
        String descricao = sc.nextLine();
        System.out.print("Duração em horas (padrão 2): ");
        int duracao = 2;
        try {
            duracao = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            // Usa 2
        }

        Evento evento = new Evento(nome, endereco, categoria, horario, descricao, duracao);
        evento.setId(nextId++);
        eventos.add(evento);
        salvarEventos();
        System.out.println("Evento '" + nome + "' cadastrado com sucesso!");
    }

    public void listarEventos() {
        if (eventos.isEmpty()) {
            System.out.println("\nNenhum evento cadastrado.");
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        List<Evento> futuros = new ArrayList<>();
        List<Evento> ocorrendo = new ArrayList<>();
        List<Evento> passados = new ArrayList<>();

        for (Evento e : eventos) {
            if (e.jaOcorrreu()) {
                passados.add(e);
            } else if (e.estaOcorrendo()) {
                ocorrendo.add(e);
            } else {
                futuros.add(e);
            }
        }

        // Ordenar por horário mais próximo
        futuros.sort(Comparator.comparing(Evento::getHorario));
        ocorrendo.sort(Comparator.comparing(Evento::getHorario));
        passados.sort(Comparator.comparing(Evento::getHorario).reversed());

        System.out.println("\n=== Eventos Ocorrendo Agora ===");
        if (ocorrendo.isEmpty()) {
            System.out.println("Nenhum evento ocorrendo no momento.");
        } else {
            for (Evento e : ocorrendo) {
                System.out.println(e.toStringDisplay());
            }
        }

        System.out.println("\n=== Eventos Futuros (ordenados por proximidade) ===");
        for (Evento e : futuros) {
            System.out.println(e.toStringDisplay());
        }

        System.out.println("\n=== Eventos Passados ===");
        for (Evento e : passados) {
            System.out.println("ID: " + e.getId() + " | " + e.getNome() + " | Data: " + e.getHorario().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " | " + e.getCategoria());
        }
    }

    public void confirmarParticipacao() {
        if (usuarioAtual == null) {
            System.out.println("Selecione um usuário primeiro.");
            return;
        }
        listarEventos();
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("\nID do evento para confirmar participação: ");
            int eventoId = Integer.parseInt(sc.nextLine());
            Optional<Evento> optEvento = eventos.stream().filter(e -> e.getId() == eventoId).findFirst();
            if (optEvento.isPresent()) {
                Evento evento = optEvento.get();
                if (!evento.jaOcorrreu()) {
                    usuarioAtual.adicionarParticipacao(eventoId);
                    System.out.println("Participação confirmada em '" + evento.getNome() + "'!");
                } else {
                    System.out.println("Evento já ocorreu.");
                }
            } else {
                System.out.println("Evento inválido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    public void listarMinhasParticipacoes() {
        if (usuarioAtual == null) {
            System.out.println("Selecione um usuário primeiro.");
            return;
        }
        List<Evento> participacoes = usuarioAtual.listarParticipacoes(eventos);
        if (participacoes.isEmpty()) {
            System.out.println("\nNenhuma participação confirmada.");
            return;
        }

        System.out.println("\n=== Minhas Participações ===");
        LocalDateTime agora = LocalDateTime.now();
        for (Evento e : participacoes) {
            String status;
            if (e.estaOcorrendo()) {
                status = "Ocorrendo agora";
            } else if (e.getHorario().isAfter(agora)) {
                status = "Futuro";
            } else {
                status = "Passado";
            }
            System.out.println("ID: " + e.getId() + " | " + e.getNome() + " | " + e.getEndereco() + " | Horário: " + e.getHorario().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " | Status: " + status);
        }
    }

    public void cancelarParticipacao() {
        if (usuarioAtual == null) {
            System.out.println("Selecione um usuário primeiro.");
            return;
        }
        listarMinhasParticipacoes();
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("\nID do evento para cancelar: ");
            int eventoId = Integer.parseInt(sc.nextLine());
            usuarioAtual.removerParticipacao(eventoId);
            System.out.println("Participação cancelada!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    private void salvarEventos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoEventos))) {
            for (Evento e : eventos) {
                writer.println(e.toString());
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }

    private void carregarEventos() {
        File file = new File(arquivoEventos);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            eventos.clear();
            String linha;
            int id = 1;
            while ((linha = reader.readLine()) != null) {
                try {
                    Evento e = Evento.fromString(linha, id++);
                    eventos.add(e);
                } catch (Exception e) {
                    System.out.println("Erro ao ler evento: " + e.getMessage());
                }
            }
            nextId = id;
        } catch (IOException e) {
            System.out.println("Erro ao carregar eventos: " + e.getMessage());
        }
    }

    public void menu() {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Sistema de Eventos da Cidade ===");
            System.out.println("1. Cadastrar Usuário");
            System.out.println("2. Selecionar Usuário");
            System.out.println("3. Cadastrar Evento");
            System.out.println("4. Listar Eventos");
            System.out.println("5. Confirmar Participação");
            System.out.println("6. Listar Minhas Participações");
            System.out.println("7. Cancelar Participação");
            System.out.println("0. Sair");
            
            System.out.print("Escolha uma opção: ");
            String op = sc.nextLine().trim();
            
            switch (op) {
                case "1":
                    cadastrarUsuario();
                    break;
                case "2":
                    selecionarUsuario();
                    break;
                case "3":
                    cadastrarEvento();
                    break;
                case "4":
                    listarEventos();
                    break;
                case "5":
                    confirmarParticipacao();
                    break;
                case "6":
                    listarMinhasParticipacoes();
                    break;
                case "7":
                    cancelarParticipacao();
                    break;
                case "0":
                    salvarEventos();
                    System.out.println("Até logo!");
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    public static void main(String[] args) {
        SistemaEventos sistema = new SistemaEventos();
        sistema.menu();
    }
}