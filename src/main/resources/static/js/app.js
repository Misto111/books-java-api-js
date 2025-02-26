// DOM елементи // за кой html се отнасят
const bookForm = document.getElementById('bookForm');
const booksContainer = document.getElementById('books-container');
const reloadBooksButton = document.getElementById('reloadBooks');

// Зареждане на всички книги
function loadBooks() {
    fetch('http://localhost:8080/api/books') // Извършва HTTP GET заявка за всички книги
        .then(response => response.json()) // Парсира отговорът от сървъра в JSON формат
        .then(books => {
            booksContainer.innerHTML = ''; // Изчистване на таблицата преди добавяне на нови редове
            books.forEach(book => { // Обхождаме всички книги и ги добавяме към таблицата
                const row = document.createElement('tr'); // Създаване на нов ред в таблицата
                row.innerHTML = `
                    <td>${book.title}</td>  <!-- Показване на заглавието на книгата -->
                    <td>${book.author.name}</td>  <!-- Показване на името на автора -->
                    <td>${book.isbn}</td>  <!-- Показване на ISBN на книгата -->
                    <td>
                        <button onclick="editBook(${book.id})">Edit</button>  <!-- Бутон за редактиране на книга -->
                        <button onclick="deleteBook(${book.id})">Delete</button>  <!-- Бутон за изтриване на книга -->
                    </td>
                `;
                booksContainer.appendChild(row); // Добавяме новия ред в таблицата
            });
        })
        .catch(error => console.error('Error:', error)); // Логваме грешката, ако има проблем
}

// Функция за добавяне на нова книга
function addBook(event) {
    event.preventDefault(); // Избягваме презареждането на страницата при изпращане на формата

    const book = { // Събиране на данни от формата
        title: document.getElementById('title').value,
        author: { name: document.getElementById('author').value },
        isbn: document.getElementById('isbn').value
    };

    const bookId = document.getElementById('bookId').value; // Вземане на ID-то на книгата, ако се редактира
    const method = bookId ? 'PUT' : 'POST'; // Ако има ID, правим PUT заявка, в противен случай - POST
    const url = bookId ? `http://localhost:8080/api/books/${bookId}` : 'http://localhost:8080/api/books'; // Задаваме URL в зависимост от метода

    fetch(url, { // Извършваме HTTP заявка за добавяне или актуализиране на книга
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(book) // Преобразуваме обекта book в JSON формат
    })
        .then(response => response.json()) // Парсираме отговорът от сървъра
        .then(() => {
            loadBooks(); // Презареждаме списъка с книги
            resetForm(); // Рестартираме формата
        })
        .catch(error => console.error('Error:', error)); // Логваме грешката, ако има проблем
}

// Функция за редактиране на книга
function editBook(bookId) {
    fetch(`http://localhost:8080/api/books/${bookId}`) // Извършваме HTTP GET заявка за книгата по ID
        .then(response => response.json()) // Парсираме отговорът от сървъра в JSON формат
        .then(book => {
            document.getElementById('bookId').value = book.id; // Записваме ID-то на книгата в скритото поле
            document.getElementById('title').value = book.title; // Записваме заглавието на книгата във формата
            document.getElementById('author').value = book.author.name; // Записваме името на автора във формата
            document.getElementById('isbn').value = book.isbn; // Записваме ISBN-то на книгата във формата
        })
        .catch(error => console.error('Error:', error)); // Логваме грешката, ако има проблем
}

// Функция за изтриване на книга
function deleteBook(bookId) {
    if (confirm("Are you sure you want to delete this book?")) { // Потвърждение за изтриване
        fetch(`http://localhost:8080/api/books/${bookId}`, { // Извършваме HTTP DELETE заявка за изтриване на книгата
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    loadBooks(); // Презареждаме списъка с книги след изтриване
                } else {
                    alert('Error deleting book'); // Показваме съобщение за грешка, ако не успеем да изтрием книгата
                }
            })
            .catch(error => console.error('Error:', error)); // Логваме грешката, ако има проблем
    }
}

// Рестартиране на формата
function resetForm() {
    document.getElementById('bookId').value = ''; // Изчистваме ID-то на книгата
    document.getElementById('title').value = ''; // Изчистваме полето за заглавие
    document.getElementById('author').value = ''; // Изчистваме полето за автор
    document.getElementById('isbn').value = ''; // Изчистваме полето за ISBN
}

// При натискане на бутон "LOAD ALL BOOKS"
reloadBooksButton.addEventListener('click', loadBooks); // Добавяме слушател за събитие на бутона "LOAD ALL BOOKS"

// При изпращане на формата
bookForm.addEventListener('submit', addBook); // Добавяме слушател за събитие при изпращане на формата

// Зареждаме книгите при зареждане на страницата
loadBooks(); // Първоначално зареждаме всички книги
