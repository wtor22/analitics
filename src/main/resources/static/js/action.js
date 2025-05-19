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

    fetchActions();
    setupToggleHandler(); // навешивает делегирование

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
        fetch(url, {
            method: method,
            body: formData
        })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                return response.json().then(error => {
                    throw new Error(error.message || "Ошибка при загрузке файла");
                });
            }
        })
        .then(data => {
            alert("Акция успешно сохранена!");
            fetchActions()
        })
        .catch(error => {
            console.error("Ошибка:", error);
            alert(error.message); // Тут покажется текст про превышение размера
        });
    });

    // Очищаю при закрытии Modal
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

function fetchActions() {
    fetch("/api/v1/client/actions")
        .then(response => {
            if (!response.ok) {
                throw new Error("Не удалось загрузить акции");
            }
            return response.json();
        })
        .then(actions => {
            renderActions(actions);
        })
        .catch(error => {
            console.error("Ошибка загрузки акций:", error);
        });
}

function renderActions(actions) {
    const container = document.getElementById("actions-container");
    container.innerHTML = "";

    if (!actions || actions.length === 0) {
        container.innerHTML = "<p>Пока нет доступных акций.</p>";
        return;
    }
    actions.forEach(action => {

        const orgIds = Array.isArray(action.organizationIds) ? action.organizationIds : [];
        const orgNames = orgIds.map(id => organizationMap[id]).filter(Boolean);

        // Генерируем HTML-бэйджи:
        const orgListHtml = orgNames
            .map(name => `<span class="badge bg-primary me-1">${name}</span>`)
            .join("");

        let periodText = "";
        if(action.startActionDate && action.endActionDate) {
            periodText = action.startActionDate + " - " + action.endActionDate;
        }
        if(!action.startActionDate && !action.endActionDate) {
            periodText = "Бессрочная";
        }
        if(!action.startActionDate && action.endActionDate) {
            periodText =  "действует до " + action.endActionDate;
        }
        if(action.startActionDate && !action.endActionDate) {
            periodText = "Бессрочная действует с " + action.startActionDate;
        }

        const card = document.createElement("div");
        card.className = "action-card mb-5";
        card.innerHTML = `
            <div class="d-flex mb-1">
                <div class="col-8 action-title">
                    <h3 class="d-block mb-1">${action.name}</h3>
                    <small>${periodText}</small>
                </div>
                <div class="col-4">
                    <div class="form-check form-switch d-flex">
                        <input class="form-check-input action-toggle ms-auto me-1"
                               type="checkbox"
                               role="switch"
                               id="switch-${action.id}"
                               data-id="${action.id}"
                               ${action.active ? "checked" : ""}>
                        <label class="form-check-label" for="switch-${action.id}">
                            ${action.active ? "Активно" : "Неактивно"}
                        </label>
                    </div>
                    <div id="list-org-${action.id}" class="d-flex justify-content-end">
                        ${orgListHtml}
                    </div>
                </div>
            </div>
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
        if(btn) {
            const actionId = btn.getAttribute("data-action-id");
            fetch(`/api/v1/client/actions/${actionId}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Не удалось загрузить акции");
                    }
                    return response.json();
                })
                .then(action => {
                    renderActionForm(action);
                })
                .catch(error => {
                    console.error("Ошибка загрузки акций:", error);
                });
        }
    });
}

function renderActionForm(action) {

    document.getElementById('actionId').value = action.id;
    document.getElementById("titleFormAction").textContent = action.name;
    document.getElementById("name-action").value = action.name;

    // Очистить все чекбоксы
    document.querySelectorAll('input[name="organizationIds"]').forEach(cb => cb.checked = false);

    // Отметить нужные организации
    if (action.organizationIds) {
        action.organizationIds.forEach(orgId => {
            const checkbox = document.querySelector(`input[name="organizationIds"][value="${orgId}"]`);
            if (checkbox) checkbox.checked = true;
        });
    }

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


function setupToggleHandler() {
    const container = document.getElementById("actions-container");

    container.addEventListener("change", function (event) {
        const toggle = event.target;

        if (toggle && toggle.classList.contains("action-toggle")) {
            const actionId = toggle.dataset.id;
            const active = toggle.checked;

            fetch(`/api/v1/client/actions/status`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
                },
                body: JSON.stringify(
                    { "active": active,
                      "id": actionId
                    })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Ошибка при обновлении статуса акции");
                }
                return response.text();
            })
            .then(data => {
                const label = document.querySelector(`label[for="switch-${actionId}"]`);
                if (label) {
                    label.textContent = active ? "Активно" : "Неактивно";
                }
            })
            .catch(error => {
                console.error("Ошибка отправки статуса:", error);
            });
        }
    });
}


