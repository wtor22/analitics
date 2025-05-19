document.addEventListener("DOMContentLoaded", function () {
    const imageButtonSelectedContainer = document.getElementById("imageButtonSelectedContainer");
    const createImageLinkButton = document.getElementById("create-image-link-button");
    const modalSetButton = document.getElementById("modalSetButton");
    const setButtonForm = document.getElementById("setButtonForm");

    loadImageButtonLink();

    modalSetButton.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;

        // Проверяем, передали ли что-то
        const id = button.getAttribute('data-id') || '';
        const text = button.getAttribute('data-text') || '';
        const value = button.getAttribute('data-value') || '';

        modalSetButton.querySelector('#button-id').value = id;
        modalSetButton.querySelector('#button-text').value = text;
        modalSetButton.querySelector('#button-link').value = value;
    })

    setButtonForm.addEventListener("submit", function (event) {
        event.preventDefault();
        saveButton();
    });

    function saveButton() {
        const buttonIdInput = document.getElementById("button-id");
        const buttonTextInput = document.getElementById("button-text");
        const buttonLinkInput = document.getElementById("button-link");

        if (!isValidUrl(buttonLinkInput.value.trim())) {
            alert("⚠️ Введите корректную ссылку, начиная с http:// или https://");
            return;
        }

        var method = 'POST';
        const data = {
            textButton: buttonTextInput.value.trim(),
            buttonValue: buttonLinkInput.value.trim()
        };
        if( buttonIdInput.value.length > 0) {
            data.id = buttonIdInput.value;
            method = 'PUT';
        }

        fetch('api/v1/bot/button', {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Ошибка при сохранении кнопки");
            }
            return response.text();
        })
        .then(result => {
            console.log("✅ ", result);
            // Закрыть модальное окно
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalSetButton'));
            modal.hide();
            loadImageButtonLink();
        })
        .catch(error => {
            console.error("❌ Ошибка:", error);
        });
    }

    function isValidUrl(url) {
        const trimmed = url.trim();
        const pattern = /^(https?:\/\/)[^\s$.?#].[^\s]*$/i;
        return pattern.test(trimmed);
    }

    function loadImageButtonLink() {

        fetch('api/v1/bot/button', {
            method: 'GET'
        })
        .then(response => {
            if(!response.ok)  throw new Error('Ошибка при загрузке данных');
            return response.json();
        })
        .then(data => {
            updateImageButtonSelectorContainer(data);
        })
        .catch(error => console.error("Ошибка загрузки ссылок на изображения:", error));
    }

    function updateImageButtonSelectorContainer(data) {
        imageButtonSelectedContainer.innerHTML = '';
        data.forEach(but => {
            const li = document.createElement("li");
            li.className = "sortable-item";
            li.dataset.id = but.id;

            // Кнопка ссылка
            const buttonLinkImage = document.createElement("a");
            buttonLinkImage.textContent = but.textButton;
            buttonLinkImage.href = but.buttonValue;
            buttonLinkImage.target = "_blank"; // чтобы открывалась в новой вкладке
            buttonLinkImage.classList.add("btn", "btn-outline-primary", "me-2");

            // Кнопка редактирования кнопки-ссылки
            const editButton = document.createElement("button");
            editButton.type = "button";
            editButton.classList.add("edit-btn","clear-btn")
            editButton.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" height="16" viewBox="0 0 24 24" width="16" fill="currentColor">
                                        <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM21.41 6.34a1.25 1.25 0 000-1.77l-2-2a1.25 1.25 0 00-1.77 0l-1.83 1.83 3.75 3.75 1.85-1.81z"/>
                                        </svg>`;
            editButton.setAttribute('data-id', but.id);
            editButton.setAttribute('data-text', but.textButton);
            editButton.setAttribute('data-value', but.buttonValue);
            editButton.setAttribute('data-bs-toggle',"modal");
            editButton.setAttribute('data-bs-target', '#modalSetButton');

            // Кнопка удаления кнопки-ссылки
            const removeButton = document.createElement("button");
            removeButton.type = "button";
            removeButton.classList.add("remove-btn","clear-btn");
            //removeButton.setAttribute('data-id', but.id);
            removeButton.textContent = "❌";

            // Текст URL
            const linkSpan = document.createElement("span");
            linkSpan.textContent = "  url: " + but.buttonValue;

            // Добавляем всё в элемент списка
            li.appendChild(buttonLinkImage);
            li.appendChild(linkSpan);
            li.appendChild(editButton);
            li.appendChild(removeButton);

            imageButtonSelectedContainer.appendChild(li);

            li.querySelector(".remove-btn").addEventListener("click", function () {
                li.remove();
                deleteButton(but.id);
            });

        });
    }

    function deleteButton(id) {
        fetch(`/api/v1/bot/button/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Ошибка при удалении: ${response.status}`);
            }
            return response.text();
        })
        .then(message => {
            console.log("✅ ", message);
            // Можно тут ещё обновить UI — убрать кнопку, показать сообщение и т.п.
        })
        .catch(error => {
            console.error('Произошла ошибка:', error);
        });
    }

    function saveButtonOrderedToBackend() {
        const items = document.querySelectorAll("#imageButtonSelectedContainer li");
        const orderedIds = Array.from(items).map(li => li.dataset.id);

        fetch('api/v1/bot/button/order', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            },
            body: JSON.stringify(orderedIds)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при сохранении порядка');
            }
            return response.json();
        })
        .then(data => {
            console.log("✅ ", "Порядок кнопок обновлен");
        })
        .catch(error => {
            console.error("Ошибка при отправке порядка кнопок:", error);
        });
    }

    Sortable.create(imageButtonSelectedContainer, {
        animation: 150,
        onEnd: function () {
            const items = imageButtonSelectedContainer.querySelectorAll("li");

            items.forEach(li => {
                const input = li.querySelector("input[name='categoryIds']");
                if (input) li.removeChild(input);
            });

            items.forEach(li => {
                const newInput = document.createElement("input");
                newInput.type = "hidden";
                newInput.name = "categoryIds";
                newInput.value = li.dataset.id;
                li.appendChild(newInput);
            });

            saveButtonOrderedToBackend();
        }
    });
})