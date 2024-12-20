# Aplicativo de Corrida com Sensores

Este projeto é um aplicativo Android de corrida baseado em sensores, onde múltiplos carros autônomos competem em uma pista simulada. Cada carro utiliza sensores de distância para evitar colisões e escolher a melhor rota, enquanto o estado de cada um pode ser pausado e retomado, além de ser salvo no FireStore.

## Funcionalidades

- **Movimentação com Sensores**: Cada carro utiliza sensores que simulam a varredura de obstáculos ao seu redor para decidir a direção de movimento.
- **Controle de Corrida**: A corrida pode ser pausada e retomada. Ao pausar, o estado dos carros é salvo no FireStore.
- **Simulação Multicarro**: Permite adicionar múltiplos carros que se movem simultaneamente pela pista.
- **Contagem de Voltas**: Cada carro tem um contador de voltas para o progresso da corrida.
- **Prioridade de Movimento**: O primeiro carro da lista tem prioridade máxima durante o movimento.
- **Interrupção e Retomada de Corrida**: Um botão de pausa altera o texto para 'Retomar' ao ser pressionado e vice-versa.

## Estrutura do Projeto

- **MainActivity**: Controla o fluxo principal da corrida e o estado de pausa.
- **Classe Car**: Define os atributos e métodos de cada carro, incluindo sensores, direção, e contagem de voltas.
- **FireStore**: Integração para salvar o último estado dos carros ao pausar ou finalizar a corrida.
- **Canvas para Renderização**: Utiliza um `Canvas` para desenhar a posição e a direção dos carros em tempo real.

## Tecnologias e Bibliotecas Utilizadas

- **Java**: Linguagem de programação principal para lógica de negócios e manipulação de UI.
- **Android SDK**: Para desenvolvimento do aplicativo Android.
- **FireStore**: Para armazenamento e recuperação de estado dos carros.
- **Threads e Handlers**: Gerenciamento de movimentação em tempo real dos carros.
- **JUnit Tests**: Testes unitários utilizando JUnit e Mockito.
- **Biblioteca**: Criação e utilização de uma biblioteca personalizada.

## Criação de uma Biblioteca Personalizada no Android

Caso tenha interesse em criar sua própria biblioteca personalizada no Android para modularizar funcionalidades como cálculos matemáticos ou utilização do banco de dados, confira o vídeo abaixo. Ele oferece uma explicação completa e passo a passo do processo de criação de bibliotecas no Android:

[![Step-by-Step Guide to Publish your Android Library!](https://img.youtube.com/vi/h5awdxf4k2I/maxresdefault.jpg)](https://www.youtube.com/watch?v=h5awdxf4k2I)

Ou assista diretamente clicando [aqui](https://www.youtube.com/watch?v=h5awdxf4k2I).

## Instruções para Rodar o Projeto

1. Clone este repositório:
   ```bash
   git clone https://github.com/sergioperess/aplicativo_corrida.git
   ```   
2. Abra o projeto no Android Studio e faça o build.
3. Configure as credenciais do FireStore no projeto para integração.
4. Execute o aplicativo em um dispositivo físico ou emulador Android.

