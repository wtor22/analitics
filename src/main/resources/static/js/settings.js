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
            method: 'PUT'
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

//    // Загружаем все категории товаров
//    function loadCategories() {
//    fetch('api/v1/client/product/category')
//        .then(response => {
//            if (!response.ok) {
//                throw new Error("Ошибка загрузки категорий");
//            }
//            return response.json();
//        })
//        .then(data => {
//            categoryContainer.innerHTML = '';
//
//            // фильтруем и сортируем пути
//            const sortedPaths = Object.keys(data)
//                .filter(path => path && path.trim() !== '') // убираем только пустые пути!
//                .sort();
//
//            for(const path of sortedPaths) {
//                const group = data[path];
//
//                const groupDiv = document.createElement('div');
//                groupDiv.classList.add("p-3")
//                const groupHeader = document.createElement('h6');
//                groupHeader.style.textDecoration = "underline";
//                groupHeader.textContent = `${path}`;
//                groupDiv.appendChild(groupHeader);
//                categoryContainer.appendChild(groupDiv);
//
//                group.forEach(category => {
//                    const categoryDiv = document.createElement('div');
//                    categoryDiv.classList.add("form-check");
//                    const categoryInput =  document.createElement('input');
//                    categoryInput.type = "checkbox";
//                    categoryInput.id = category.id;
//
//                    categoryInput.classList.add("form-check-input");
//
//                    const categoryInputLabel = document.createElement('label');
//                    categoryInputLabel.textContent = category.name
//                    categoryInputLabel.classList.add("form-check-label");
//                    categoryInputLabel.setAttribute("for", `${category.id}`);
//
//                    categoryDiv.appendChild(categoryInput);
//                    categoryDiv.appendChild(categoryInputLabel);
//                    groupDiv.appendChild(categoryDiv);
//                })
//            }
//            console.log("Категории получены:", data);
//            // здесь можно отрисовать категории или что-то с ними сделать
//        })
//        .catch(error => {
//            console.error("Ошибка:", error);
//        });
//
//    }
    // Загружаем менеджеров и категории при старте
    //loadManagers();
    //loadCategories();


});