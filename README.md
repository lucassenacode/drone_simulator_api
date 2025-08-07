# Simulador de Drones Urbanos

Este projeto é uma solução para o desafio técnico de simular a operação de drones urbanos para a entrega de pacotes. A aplicação foi desenvolvida em Spring Boot e implementa uma API RESTful para gerenciar drones e pacotes, com uma lógica de alocação otimizada e um serviço de simulação agendado.

## Tecnologias Utilizadas

- **Java 21**: Linguagem de programação.
- **Spring Boot**: Framework para o desenvolvimento da aplicação.
- **Spring Data JPA**: Camada de acesso a dados.
- **H2 Database**: Banco de dados em memória para persistência dos dados.
- **Lombok**: Biblioteca para reduzir o código clichê (getters, setters, etc.).
- **Maven**: Gerenciador de dependências e build do projeto.
- **Postman**: Utilizado para testar os endpoints da API.

## Funcionalidades Implementadas

- **Gerenciamento de Entidades**: Classes `Drone` e `Package` com suas respectivas propriedades, como capacidade de peso, alcance de voo, localização e estado.
- **API RESTful**: Endpoints para criar, listar e gerenciar drones e pacotes.
- **Persistência de Dados**: O banco de dados H2 está configurado em modo de arquivo (`file`), garantindo que os dados persistam entre as reinicializações da aplicação.
- **Lógica de Alocação**: Um serviço de otimização aloca pacotes pendentes para drones ociosos, respeitando as restrições de peso e distância.
- **Simulação de Entrega**: Um serviço agendado (`@Scheduled`) move automaticamente os drones através de um ciclo de estados (`IDLE` -> `LOADING` -> `IN_FLIGHT` -> `DELIVERING` -> `RETURNING` -> `IDLE`), simulando o processo de entrega a cada 5 segundos.
- **Tratamento de Exceções**: A API lida com erros de forma controlada, retornando status `404 Not Found` para recursos não encontrados e `400 Bad Request` para entradas inválidas.

## Como Executar a Aplicação

1.  **Pré-requisitos**: Certifique-se de ter o **Java 21** e o **Maven** instalados na sua máquina.
2.  **Navegue até a pasta do projeto**: Abra o terminal e vá para a pasta raiz do projeto.
3.  **Execute a aplicação**: Rode o comando abaixo para iniciar o Spring Boot.
    ```bash
    mvn spring-boot:run
    ```
4.  **Acesse o H2 Console**: Com a aplicação rodando, você pode acessar o console do banco de dados no navegador para visualizar as tabelas e dados.
    - URL: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:file:./dronedb`
    - User Name: `sa`

## Endpoints da API

A base da sua API é `http://localhost:8080/api`. Abaixo estão os endpoints principais, organizados por funcionalidade.

### Drones

| Método | Endpoint                                   | Descrição                                          |
| :----- | :----------------------------------------- | :------------------------------------------------- |
| `POST` | `/drones`                                  | Cria um novo drone.                                |
| `GET`  | `/drones/status`                           | Retorna o status de todos os drones.               |
| `GET`  | `/drones/{droneId}`                        | Retorna os detalhes de um drone específico.        |
| `PUT`  | `/drones/{droneId}/state?newState={state}` | Atualiza o estado de um drone (ex: `IN_FLIGHT`).   |
| `POST` | `/drones/allocate-pending`                 | Aciona a alocação automática de pacotes pendentes. |

### Pacotes

| Método | Endpoint                                   | Descrição                                      |
| :----- | :----------------------------------------- | :--------------------------------------------- |
| `POST` | `/packages`                                | Cria um novo pacote.                           |
| `GET`  | `/packages/pending`                        | Retorna a lista de todos os pacotes pendentes. |
| `GET`  | `/packages/delivered`                      | Retorna a lista de todos os pacotes entregues. |
| `GET`  | `/packages/{packageId}/status`             | Retorna o status de um pacote específico.      |
| `POST` | `/packages/{packageId}/allocate/{droneId}` | Aloca um pacote a um drone de forma manual.    |

## Fluxo de Teste da Simulação

1.  **Crie Drones**: Use o endpoint `POST /drones` para adicionar 1 ou mais drones.
2.  **Crie Pacotes**: Use o endpoint `POST /packages` para adicionar 1 ou mais pacotes.
3.  **Inicie a Alocação**: Use o endpoint `POST /drones/allocate-pending` para que o sistema aloque os pacotes aos drones.
4.  **Verifique a Entrega**: Use `GET /packages/pending` para ver a fila diminuir e `GET /packages/delivered` para ver os pacotes entregues.

A simulação agendada fará a transição dos estados do drone e do pacote automaticamente, e você poderá acompanhar o progresso através dos endpoints de consulta.
