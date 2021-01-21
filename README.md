[![Build Status](https://travis-ci.org/IvanBelyaev/job4j_pooh.svg?branch=main)](https://travis-ci.org/IvanBelyaev/job4j_pooh)
[![codecov](https://codecov.io/gh/IvanBelyaev/job4j_pooh/branch/main/graph/badge.svg)](https://codecov.io/gh/IvanBelyaev/job4j_pooh)

# Техническое задание - проект "Pooh JMS"

В этом проекте мы сделаем аналог асинхронной очереди RabbitMQ.

Приложение запускает Socket и ждем клиентов.

Клиенты могут быть двух типов: отправители (publisher), получатели (subscriver).

В качестве протокола будет использовать HTTP. Сообщения в формате JSON.

Существуют два режима: queue, topic.

 

Queue. 

Отправитель посылает сообщение с указанием очереди.

Получатель читает первое сообщение и удаляет его из очереди. 

Если приходят несколько получателей, то они читают из одной очереди. 

Уникальное сообщение может быть прочитано, только одним получателем.

Пример запросов.

POST /queue

{
  "queue" : "weather",
  "text" : "temperature +18 C"
}

 

GET /queue/weather

{
  "queue" : "weather",
  "text" : "temperature +18 C"
}

 

Topic.

Отправить посылает сообщение с указанием темы.

Получатель читает первое сообщение и не удаляет его из очереди. 

Если приходят несколько получателей, то они читают отдельные очереди.

 POST /topic

{
  "topic" : "weather",
  "text" : "temperature +18 C"
}

 

GET /topic/weather

{
  "topic" : "weather",
  "text" : "temperature +18 C"
}
