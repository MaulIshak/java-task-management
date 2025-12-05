# Class Diagrams: Design Patterns

Berikut adalah diagram kelas untuk Design Pattern yang diterapkan dalam proyek ini.

## 1. Builder Pattern

Digunakan pada `TaskBuilder` untuk mempermudah pembuatan objek `Task` yang memiliki banyak atribut opsional.

```mermaid
classDiagram
    class Task {
        -int id
        -String title
        -String description
        -LocalDate dueDate
        -TaskStatus status
        -User assignee
        +Task(TaskBuilder builder)
    }

    class TaskBuilder {
        -int id
        -int projectId
        -String title
        -String description
        -LocalDate dueDate
        -TaskStatus status
        -User assignee
        +TaskBuilder(int projectId, String title)
        +setDescription(String) TaskBuilder
        +setDueDate(LocalDate) TaskBuilder
        +setStatus(TaskStatus) TaskBuilder
        +setAssignee(User) TaskBuilder
        +build() Task
    }

    TaskBuilder ..> Task : creates
```

## 2. Singleton Pattern

Digunakan pada `UserSession` untuk memastikan hanya ada satu sesi pengguna yang aktif dalam aplikasi.

```mermaid
classDiagram
    class UserSession {
        -static UserSession instance
        -User currentUser
        -List~Observer~ observers
        -UserSession()
        +static getInstance() UserSession
        +startSession(User)
        +endSession()
        +isLoggedIn() boolean
        +getCurrentUser() User
    }
```

## 3. Observer Pattern

Digunakan untuk menghubungkan `UserSession` (Subject) dengan View seperti `DashboardView` (Observer), sehingga UI otomatis diperbarui saat status login berubah.

```mermaid
classDiagram
    class Subject {
        <<interface>>
        +registerObserver(Observer o)
        +removeObserver(Observer o)
        +notifyObservers()
    }

    class Observer {
        <<interface>>
        +update()
    }

    class UserSession {
        -List~Observer~ observers
        +registerObserver(Observer o)
        +removeObserver(Observer o)
        +notifyObservers()
    }

    class DashboardView {
        +update()
        +render()
    }

    class ProjectView {
        +update()
        +render()
    }

    UserSession ..|> Subject
    DashboardView ..|> Observer
    ProjectView ..|> Observer
    UserSession --> Observer : notifies
```

## 4. Composite Pattern

Diterapkan secara natural melalui struktur **JavaFX Scene Graph**. `MainLayout` (sebagai Composite) menampung komponen lain seperti `Sidebar` dan `View` (sebagai Leaf atau Composite lainnya).

```mermaid
classDiagram
    class Parent {
        +getChildren() List~Node~
    }

    class BorderPane {
        +setLeft(Node)
        +setCenter(Node)
    }

    class MainLayout {
        +Sidebar sidebar
        +View currentView
    }

    class Sidebar {
        +update()
    }

    class DashboardView {
        +render()
    }

    Parent <|-- BorderPane
    BorderPane <|-- MainLayout
    MainLayout *-- Sidebar
    MainLayout *-- DashboardView
```
