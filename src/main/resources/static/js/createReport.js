document.addEventListener("DOMContentLoaded", function () {
    const managerSelect = document.getElementById("manager");
    const tagsContainer = document.getElementById("tags-container");
    const submitButton = document.querySelector("button[type='submit']");
    const startCurrentDateInput = document.getElementById("date-current-start");
    const endCurrentDateInput = document.getElementById("date-current-end");
    const startCompareDateInput = document.getElementById("date-compare-start");
    const endCompareDateInput = document.getElementById("date-compare-end");
    const switchCheckCheckedInput = document.getElementById('switchCheckChecked');
    const buttonContainer = document.getElementById('button-container');

    document.getElementById("toggle-all").addEventListener("click", function (event) {
        event.preventDefault();
        const checkboxes = document.querySelectorAll("#tags-container input[type='checkbox']");
        const allChecked = Array.from(checkboxes).every(checkbox => checkbox.checked);
        checkboxes.forEach(checkbox => {
            checkbox.checked = !allChecked;
        });
        this.textContent = allChecked ? "Выбрать все" : "Снять все";
    });

    // Загружаем менеджеров
    function loadManagers() {
        fetch('/api/v1/client/manager')
            .then(response => response.json())
            .then(data => {
                data.forEach(manager => {
                    const option = document.createElement("option");
                    option.value = manager.id;
                    option.textContent = manager.name;
                    managerSelect.appendChild(option);
                });
            })
            .catch(error => console.error("Ошибка загрузки менеджеров:", error));
    }

    // Загружаем теги для выбранного менеджера
    function loadTags(managerId) {

        tagsContainer.innerHTML = '<p class="text-muted">Загрузка...</p>';

        fetch(`/api/v1/client/manager/tags?managerId=${managerId}`)
            .then(response => response.json())
            .then(data => {
                tagsContainer.innerHTML = ""; // Очищаем контейнер

                if (data.length === 0) {
                    tagsContainer.innerHTML = '<p class="text-danger">Нет доступных тегов</p>';
                    submitButton.disabled = true;
                    buttonContainer.classList.add('d-none');
                    return;
                }
                buttonContainer.classList.remove('d-none');
                data.forEach(tag => {

                    const div = document.createElement("div");
                    div.classList.add("form-check", "mb-2");

                    const input = document.createElement("input");
                    input.type = "checkbox";
                    input.checked = true;
                    input.classList.add("btn-check");
                    const label = document.createElement('label');
                    label.classList.add("btn");
                    label.setAttribute('for', `tag-${tag.id}`);

                    input.id = `tag-${tag.id}`;
                    input.value = tag.id;
                    input.name = "tags";

                    label.textContent = tag.tag;
                    div.appendChild(input);
                    div.appendChild(label);
                    tagsContainer.appendChild(div);
                });


                submitButton.disabled = false; // Разблокируем кнопку
            })
            .catch(error => {
                tagsContainer.innerHTML = '<p class="text-danger">Ошибка загрузки тегов</p>';
                console.error("Ошибка загрузки тегов:", error);
            });
    }

    // Загружаем менеджеров и категории при старте
    loadManagers();

    // Обработчик выбора менеджера
    managerSelect.addEventListener("change", function () {
        const managerId = managerSelect.value;
        if (managerId) {
            loadTags(managerId);
        } else {
            tagsContainer.innerHTML = '<p class="text-muted">Сначала выберите менеджера</p>';
            submitButton.disabled = true;
        }
    });

    // Обработчик отправки формы
    document.getElementById("selectionForm").addEventListener("submit", function (event) {
        event.preventDefault();

        const checkedInputs = document.querySelectorAll('#category-container .form-check-input.check-category:checked');
        const selectedIds = Array.from(checkedInputs).map(input => input.id);
        // Просто объединяем в строку с запятыми
        const idsString = selectedIds.join(',');

        const downloadBtn = document.getElementById("download-btn");
        const spinner = document.getElementById("spinner");

        // Показываем спиннер
        spinner.classList.remove("d-none");
        downloadBtn.disabled = true; // Блокируем кнопку

        const startCurrentDate = startCurrentDateInput.value;
        const endCurrentDate = endCurrentDateInput.value;
        const startCompareDate = startCompareDateInput.value;
        const endCompareDate = endCompareDateInput.value;

        //const switchCheckChecked = switchCheckCheckedInput.value;

        const selectedManager = managerSelect.value;
        const selectedTags = Array.from(tagsContainer.querySelectorAll("input[type='checkbox']:checked"))
                                 .map(checkbox => checkbox.value);

        // Формируем URL с параметрами
        const params = new URLSearchParams({
            managerId: selectedManager,
            listIdTags: selectedTags,
            startPeriod: startCurrentDate,
            endPeriod: endCurrentDate,
            comparisonPeriodStart: startCompareDate,
            comparisonPeriodEnd: endCompareDate,
            isRememberCategorySelection: switchCheckCheckedInput.checked,
            listUUIDCategory: idsString
        });

        fetch(`/api/v1/client/report/download?${params.toString()}`, {
            method: 'GET',
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Ошибка загрузки: ${response.status}`);
            }

            // Извлекаем имя файла из заголовка, если оно передано
            const contentDisposition = response.headers.get("Content-Disposition");
            let filename = "report.xlsx"; // Значение по умолчанию

            if (contentDisposition) {
                const match = contentDisposition.match(/filename="?(.+?)"?$/);
                if (match) {
                    filename = match[1];
                }
            }

            return response.blob().then(blob => ({ blob, filename }));
        })
        .then(({ blob, filename }) => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            a.remove();
        })
        .catch(error => console.error("Ошибка загрузки:", error))
        .finally(() => {
            // Прячем спиннер и разблокируем кнопку
            spinner.classList.add("d-none");
            downloadBtn.disabled = false;
        });
    });
});

