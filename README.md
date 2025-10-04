# Sistema de Eventos da Cidade
 
# Descrição
#### O Sistema de Eventos da Cidade é um aplicativo console desenvolvido em Java, seguindo o paradigma de programação orientada a objetos (POO). Ele permite o cadastro e gerenciamento de eventos urbanos, como festas, shows, eventos esportivos e culturais, na cidade onde o usuário reside. Os usuários podem se cadastrar, confirmar participação em eventos, visualizar listas filtradas por status (futuros, ocorrendo agora ou passados) e cancelar inscrições.
#### O sistema utiliza LocalDateTime para controle de horários reais, ordenação por proximidade e verificação de eventos em andamento. A persistência dos eventos é feita em um arquivo de texto (events.data), garantindo que os dados sejam carregados automaticamente ao iniciar o programa.
#### Este projeto foi desenvolvido como exercício acadêmico, com ênfase em boas práticas de POO, como encapsulamento, herança (implícita via enums) e composição. O padrão MVC é adotado de forma implícita: classes de modelo (Usuario e Evento), controlador (SistemaEventos) e view via console.

# Funcionalidades Principais
- Cadastro de Usuários: Registre-se com nome, email, telefone e idade (opcional).
- Cadastro de Eventos: Crie eventos com nome, endereço, categoria (Festa, Esportivo, Show, Cultural ou Outros), horário (formato: yyyy-MM-dd HH:mm), descrição e duração (em horas).

#### Listagem de Eventos:
- Eventos ocorrendo agora (baseado na duração).
- Eventos futuros, ordenados por horário mais próximo.
- Eventos passados, ordenados por data recente.


# Gerenciamento de Participações:
- Confirme presença em eventos futuros.
- Visualize suas participações com status (Futuro, Ocorrendo agora ou Passado).
- Cancele participação em qualquer evento.
- Persistência: Eventos salvos em events.data (formato texto delimitado por ) e carregados na inicialização.
- Suporte a Múltiplos Usuários: Selecione entre usuários cadastrados para gerenciar participações.

# Requisitos
- Java: Versão 8 ou superior (utiliza java.time para datas).
- Ambiente: Qualquer IDE (Eclipse, IntelliJ) ou linha de comando para compilar e executar.
- Sem dependências externas (bibliotecas padrão do Java).

# Instalação e Execução
## Clone o Repositório:
textgit clone <URL_DO_REPOSITORIO>
cd sistema-eventos-cidade

## Compile o Código:
textjavac *.java

## Execute o Programa:
textjava SistemaEventos


# Siga o Menu Interativo:
- Use as opções numéricas para navegar.
- Exemplo de entrada de horário: 2025-10-05 20:00.

- O arquivo events.data será criado automaticamente na pasta do projeto para armazenar os eventos.
Estrutura do Projeto

# O projeto é organizado em classes principais:

- Usuario: Gerencia dados do usuário e lista de participações (IDs de eventos).
- Evento: Representa um evento com atributos obrigatórios e métodos para status temporal.
- SistemaEventos: Controlador principal, com menu, persistência e lógica de negócio.
- Categoria: Enum para delimitar categorias de eventos.

# Diagrama de Classes (UML-like em ASCII)




# Exemplo de Uso

- Inicie o programa e cadastre um usuário (opção 1).
- Selecione o usuário (opção 2).
- Cadastre um evento (opção 3), ex: "Show de Rock" em "Praça Central" às "2025-10-10 19:00".
- Liste eventos (opção 4) para ver a categorização.
- Confirme participação (opção 5) usando o ID do evento.
- Visualize suas participações (opção 6).
- Saia (opção 0) – os eventos são salvos automaticamente.


# Limitações e Melhorias Futuras
- Persistência de Usuários: Atualmente em memória; poderia ser estendida para arquivo.
- Validações: Básicas implementadas; adicionar mais robustez (ex: email válido).
- Interface Gráfica: Migrar para Swing ou JavaFX para uma UI mais amigável.
- Notificações: Integrar com email/SMS para alertas reais.
- Banco de Dados: Substituir arquivo por SQLite ou PostgreSQL para escalabilidade.

# Contribuições
- Contribuições são bem-vindas! Crie um fork, faça suas alterações e envie um pull request. Para issues ou sugestões, abra uma discussão no repositório.

# Licença
- Este projeto está sob a licença MIT. Veja o arquivo LICENSE para detalhes.

# Autor
- Desenvolvido por [Erik Marta Garcia] em Outubro de 2025.
- Contato: [erik.martaneva@gmail.com]
