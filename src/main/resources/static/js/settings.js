document.addEventListener("DOMContentLoaded", function () {
    const formDemandsUpdater = document.getElementById("demands-updater");
    const dateFromInput = document.getElementById("date-demand-start");
    const dateToInput = document.getElementById("date-demand-end");
    const stringResponse = document.getElementById("demands-updater-response");
    const categoryContainer = document.getElementById("category-container");
    const ownersUpdaterForm = document.getElementById("owners-updater");

    //const managerSelect = document.getElementById("manager");

    ownersUpdaterForm.addEventListener("submit", function (event) {
            event.preventDefault();
            setManagers();
    });
    function setManagers() {
        const checkedInputs = document.querySelectorAll('#owner-container .form-check-input:checked');
        const selectedOwners = Array.from(checkedInputs).map(input => input.id);

        // Объединяем в строку с запятыми
        const selectedOwnersString = selectedOwners.join(',');

        const params = new URLSearchParams({
            listManagersUUID: selectedOwnersString
        });

        fetch(`/api/v1/client/manager?${params.toString()}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Ошибка : ${response.status}`);
            }
        });
    }


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

    function loadDemands() {
        const dateStart = dateFromInput.value;
        const dateEnd = dateToInput.value;

        stringResponse.classList.add("d-none");

        // Формируем URL с параметрами
        const params = new URLSearchParams({
            start: dateStart,
            end: dateEnd
        });

        fetch(`/api/v1/client/demand?${params.toString()}`, {
            method: 'GET',
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Ошибка обработки: ${response.status}`);
            }
            return response.text();
        })
        .then(data => {

            stringResponse.textContent = data;
            stringResponse.classList.remove("d-none");
        })
        .catch(error => {
            stringResponse.textContent = `Ошибка: ${error.message}`;
        });
    }

    formDemandsUpdater.addEventListener("submit", function (event) {
        event.preventDefault(); // чтобы форма не перезагружала страницу
        loadDemands(); // запускаем наш fetch
    });

});