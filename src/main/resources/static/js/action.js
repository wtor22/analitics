$(document).ready(function() {
  $('#editor').summernote({
    lang: 'ru-RU',
    height: 300,
    toolbar: [
    ['style', ['bold', 'italic', 'underline', 'clear']],
    ['color', ['color']],
    ['para', ['ul', 'ol', 'paragraph']],
    ['insert', ['link']],
    ['view', ['codeview']]
    ]
  });

  $('#descriptionEditor').summernote({
    lang: 'ru-RU',
    height: 150,
    toolbar: [
    ['style', ['bold', 'italic', 'underline', 'clear']],
    ['color', ['color']],
    ['para', ['ul', 'ol', 'paragraph']],
    ['insert', ['link']],
    ['view', ['codeview']]
    ]
  });
});




document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("formCreateAction");

    actionListRefresh();

    // Обновление списка акций
    function actionListRefresh() {

        console.log("REFRESH")
        fetch("/api/v1/client/actions")
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Не удалось загрузить акции");
                    }
                    return response.json();
                })
                .then(actions => {
                    const container = document.getElementById("actions-container");
                    container.innerHTML = ""; // очищаем перед вставкой

                    if (actions.length === 0) {
                        container.innerHTML = "<p>Пока нет доступных акций.</p>";
                        return;
                    }

                    actions.forEach(action => {
                        const card = document.createElement("div");
                        card.className = "action-card mb-5";

                        card.innerHTML = `
                            <div class="action-title"><h3><strong>${action.name}</strong>  <small>  действует с <strong>${action.startActionDate}</strong> по <strong>${action.endActionDate}</strong></small></h3></div>
                            <div class="action-image">
                                <img src="${action.titleImageUrl}" alt="${action.name}" style="max-width: 100%; height: auto;">
                            </div>
                            <div class="action-content">
                                <p>${action.description}</p>
                                <a class="">подробнее</a>
                                <button class="edit-btn clear-btn" data-action-id="${action.id}" data-bs-target="#modalAction" data-bs-toggle="modal"><svg xmlns="http://www.w3.org/2000/svg" height="16" viewBox="0 0 24 24" width="16" fill="currentColor">
                                                                             <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM21.41 6.34a1.25 1.25 0 000-1.77l-2-2a1.25 1.25 0 00-1.77 0l-1.83 1.83 3.75 3.75 1.85-1.81z"/>
                                                                             </svg></button>
                            </div>
                        `;
                        container.appendChild(card);
                    });
                    container.addEventListener("click", (e) => {
                        const btn = e.target.closest(".edit-btn");
                        if (btn) {
                            const actionId = btn.getAttribute("data-action-id");

                            // 2. Найдем нужную акцию по id
                            const action = actions.find(a => a.id == actionId);
                            if (!action) return;
                            document.getElementById('actionId').value = action.id;
                            document.getElementById("titleFormAction").textContent = action.name;
                            document.getElementById("name-action").value = action.name;
                            // Для summernote своя приблуда
                            $('#descriptionEditor').summernote('code', action.description);
                            $('#editor').summernote('code', action.content);

                            document.getElementById("date-current-start").value = action.startActionDate;
                            document.getElementById("date-current-end").value = action.endActionDate;

                            const imageContainer = document.getElementById("image-container");

                            // Очищаем старую картинку, если была
                            imageContainer.innerHTML = "";

                            // Создаём новый img и вставляем
                            const img = document.createElement("img");
                            img.src = action.titleImageUrl;
                            img.alt = action.name;
                            img.style.maxWidth = "100%";
                            img.style.height = "auto";

                            imageContainer.appendChild(img);
                        }
                    });
                })
                .catch(error => {
                    console.error("Ошибка загрузки акций:", error);
                    const container = document.getElementById("actions-container");
                    container.innerHTML = "<p>Ошибка при загрузке акций. Попробуйте позже.</p>";
                });
    }

    // Отправка формы
    form.addEventListener("submit", function (event) {
        event.preventDefault();
        const formData = new FormData(form);

        const actionId = document.getElementById('actionId').value.trim();

        let url = "/api/v1/client/actions/create";
        let method = "POST";

        if(actionId !="") {
            url = "/api/v1/client/actions";
            method = "PUT";
        }

        console.log("PRINT FORMDATA ", formData)

        fetch(url, {
            method: method,
            body: formData
        })
        .then(response => {
            if (response.ok) {
                return response.text(); // или response.json(), если ты возвращаешь JSON
            } else {
                return response.json().then(error => {
                    throw new Error(error.message || "Ошибка при загрузке файла");
                });
            }
        })
        .then(data => {
            // Тут можно показать уведомление, очистить форму или что-то другое
            console.log("Успешно отправлено:", data);
            alert("Акция успешно создана!");
            form.reset(); // если нужно очистить
            actionListRefresh();
        })
        .catch(error => {
            console.error("Ошибка:", error);
            alert(error.message); // Тут покажется текст про превышение размера
        });

    });


    // Очищаю при закрытии
    const modalElement = document.getElementById('modalAction');

    modalElement.addEventListener('hidden.bs.modal', function () {
        // Очистка текстовых полей
        document.getElementById("titleFormAction").textContent = '';
        document.getElementById("date-current-start").value = '';
        document.getElementById("date-current-end").value = '';
        document.querySelector('input[name="id"]').value = '';

        // Очистка редактора Summernote
        $('#descriptionEditor').summernote('reset');
        $('#editor').summernote('reset');

        // Очистка картинки
        const imageContainer = document.getElementById("image-container");
        imageContainer.innerHTML = '';
    });
});


