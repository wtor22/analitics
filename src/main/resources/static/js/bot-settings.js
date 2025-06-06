document.addEventListener("DOMContentLoaded", function () {
    const sortableList = document.getElementById("sortableCategories");
    const selectedStores = document.getElementById("selectedStores");
    const storeSelectorContainer = document.getElementById("storeSelectorContainer");
    const modalSetStoreAlias = document.getElementById("modalSetStoreAlias");
    const form = document.querySelector('#setAliasForm');
    const buttonUpdateStock = document.getElementById("buttonUpdateStock");
    // Для установки админов бота
    const phoneInput = document.getElementById("phone-search");
    const searchBtn = document.getElementById("search-user-btn");
    const resultsList = document.getElementById("search-results");
    const adminList = document.getElementById("admin-list");
    const listAdmins = document.getElementById("list-admins");

    fetchCategories();
    fetchStores();
    fetchAdmins();


    searchBtn.addEventListener("click", () => {
        const query = phoneInput.value.trim();

        if (query.length < 3) {
            alert("⚠️ Введите минимум 3 символа");
            return;
        }
        fetch(`/api/v1/bot/users/search?phone=${encodeURIComponent(query)}`)
            .then(response => {
                if (!response.ok) throw new Error("Ошибка поиска");
                return response.json();
            })
            .then(users => {
                resultsList.innerHTML = '';
                if (users.length < 1) {
                    alert("⚠️ Не найден ни один пользователь с таким номером телефона");
                    return;
                }
                users.forEach(user => {
                    const li = document.createElement("li");
                    li.classList.add("d-flex", "align-items-center", "mb-2")
                    li.textContent = `${user.firstName} ${user.lastName} (${user.phoneNumber})`;

                    const addBtn = document.createElement("button");
                    addBtn.classList.add("btn","btn-danger", "ms-2")
                    addBtn.textContent = "Установить админом";
                    addBtn.addEventListener("click", () => {
                        addAdmin(user.telegramId);
                    });
                    li.appendChild(addBtn);
                    resultsList.appendChild(li);
                });
                const clsBtn = document.createElement("button");
                clsBtn.classList.add("btn","btn-secondary", "mt-3")
                clsBtn.textContent = "Отменить";
                clsBtn.addEventListener("click", () => {
                    clearListSearch();
                });
                resultsList.appendChild(clsBtn);
                resultsList.classList.remove("d-none");
            })
            .catch(err => {
                console.error("❌ Ошибка при поиске:", err);
            });
    });

    function clearListSearch() {
        resultsList.innerHTML = '';
        resultsList.classList.add("d-none");
    }

    function fetchAdmins() {
        fetch(`/api/v1/bot/users/admins`)
        .then(response => response.json())
        .then(data => updateListAdmins(data))
        .catch(error => console.error("Ошибка загрузки списка администраторов:", error));
    }

    function updateListAdmins(admins) {
        listAdmins.innerHTML = '';
        admins.forEach(admin => {
            const li = document.createElement("li");
            const spanName = document.createElement("span");
            spanName.textContent = admin.firstName + " " + admin.lastName + " тел. " + admin.phoneNumber;

            // Кнопка удаления админа
            const removeButton = document.createElement("button");
            removeButton.type = "button";
            removeButton.classList.add("remove-btn","clear-btn");
            removeButton.textContent = "❌";

            removeButton.addEventListener("click", function () {
                li.remove();
                deleteAdmin(admin.telegramId);
            });

            li.appendChild(spanName);
            li.appendChild(removeButton);
            listAdmins.appendChild(li);
        });
    }

    function deleteAdmin(userId) {
        fetch(`/api/v1/bot/users/delete-admin?userId=${userId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            }
        })
        .then(response => {
            if (!response.ok) throw new Error("Не удалось удалить");
            return response.text();
        })
        .then( data => {
            resultsList.innerHTML = '';
            alert("⚠️ " + data);
            fetchAdmins();
        })
        .catch(err => {
            console.error("❌ Ошибка при удалении:", err);
        });
    }

    function addAdmin(userId) {
        fetch(`/api/v1/bot/users/set-admin?userId=${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            }
        })
        .then(response => {
            if (!response.ok) throw new Error("Не удалось добавить админа");
            return response.text();
        })
        .then( data => {
            resultsList.innerHTML = '';
            clearListSearch();
            alert("⚠️ " + data);
            fetchAdmins();
        })
        .catch(err => {
            console.error("❌ Ошибка при добавлении админа:", err);
        });
    }

    buttonUpdateStock.addEventListener("click", function (event) {
        loadStocks(); // запускаем fetch
    });

    function loadStocks() {
        const buttonText = document.getElementById("buttonText");
        const spinnerStock = document.getElementById("spinnerStock");
        const spinnerStockText = document.getElementById("spinnerStockText");

        buttonText.classList.add("d-none");
        spinnerStock.classList.remove("d-none");
        spinnerStockText.classList.remove("d-none");
        buttonUpdateStock.disabled = true;

        fetch('api/v1/client/report/stock/by-store', {
            method: 'GET'
        })
        .then(response => {
            if(!response.ok)  throw new Error('Ошибка при загрузке данных');
            return response.text();
        })
        .then(data => {
            const updateStockResponse = document.getElementById("updateStockResponse");
            updateStockResponse.classList.remove("d-none");
            updateStockResponse.textContent = data;
            buttonText.classList.remove("d-none");
            spinnerStock.classList.add("d-none");
            spinnerStockText.classList.add("d-none");
            buttonUpdateStock.disabled = false;
        })
        .catch(error => {
            updateStockResponse.classList.add("text-danger");
            updateStockResponse.textContent = "Ошибка при загрузке: " + error.message;
            console.error("Ошибка при загрузке:", error);
            buttonText.classList.remove("d-none");
            spinnerStock.classList.add("d-none");
            spinnerStockText.classList.add("d-none");
            buttonUpdateStock.disabled = false;
        });
    }

    form.addEventListener('submit', function (event) {
        event.preventDefault();
        console.log("START SET ALIAS")
        saveStoreAliasToBackend();
    });

    modalSetStoreAlias.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const storeId = button.getAttribute('data-store-id');
        const storeName = button.getAttribute('data-store-name');
        const modalStoreId = modalSetStoreAlias.querySelector('#modal-store-id');
        const modalStoreName = modalSetStoreAlias.querySelector('#modal-store-name');
        modalStoreId.value = storeId;
        modalStoreName.textContent = storeName;
    });

    function saveStoreAliasToBackend() {
        const alias = document.querySelector("#setAliasForm #alias").value;
        const id = document.querySelector("#setAliasForm #modal-store-id").value;
        const data = {
            id: id,
            alias: alias
        };
        fetch('api/v1/client/store/set-alias', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при сохранении порядка');
                }
                return response.json();
            })
            .then(data => {
                // Закрыть модальное окно
                const modal = bootstrap.Modal.getInstance(document.getElementById('modalSetStoreAlias'));
                modal.hide();
                updateSelectedStores(data.listDtoExistingInStockReport);
            })
            .catch(error => {
                console.error("Ошибка при отправке порядка складов:", error);
            });
    }

    function fetchStores() {
         fetch('api/v1/client/store/getStores')
         .then(response => response.json())
         .then(data => {
             updateStoreSelectorContainer(data.listDtoNotExistingInStockReport);
             updateSelectedStores(data.listDtoExistingInStockReport);
         })
         .catch(error => console.error("Ошибка загрузки складов:", error));
    }

    function updateStoreSelectorContainer(stores) {
        storeSelectorContainer.innerHTML = '';
        if (stores.length === 0) return;
        const select = document.createElement("select");
        select.id = "storeSelector";
        select.className = "form-select";
        const defaultOption = document.createElement("option");
        defaultOption.value = "";
        defaultOption.disabled = true;
        defaultOption.selected = true;
        defaultOption.textContent = "Выберите склад";
        select.appendChild(defaultOption);
        stores.forEach(store => {
            const option = document.createElement("option");
            option.value = store.id;
            option.textContent = store.name;
            select.appendChild(option);
        });

        select.addEventListener("change", function () {
            const selectedId = this.value;
            const selectedName = this.options[this.selectedIndex].text;
            if (selectedId) {
                addStoreToSelectedStores(selectedId, selectedName);
                this.selectedIndex = 0;
            }
        });
        storeSelectorContainer.appendChild(select);
    }

    function updateSelectedStores(stores) {
        selectedStores.innerHTML = "";
        stores.forEach(store => {
            createLiStoreElement(store.id, store.name,store.nameToBot)
        })
    }

    function addStoreToSelectedStores(id, name, nameToBot) {
        createLiStoreElement(id, name, nameToBot)
        saveStoreOrderToBackend();
    }

    function createLiStoreElement(id, name, nameToBot) {
        if (document.querySelector(`#selectedStores li[data-id="${id}"]`)) {
            return;
        }
        const span = document.createElement("span");
        span.textContent = name + (nameToBot ? " (" + nameToBot + ")" : "");
        span.style.cssText = "margin-right: 10px; vertical-align: bottom;";

        const li = document.createElement("li");
        li.className = "sortable-item";
        li.dataset.id = id;

        // Кнопка редактирования склада
        const editButton = document.createElement("button");
        editButton.type = "button";
        editButton.setAttribute('data-store-id', id);
        editButton.setAttribute('data-store-name', name);
        editButton.setAttribute('data-bs-toggle', 'modal');
        editButton.setAttribute('data-bs-target', '#modalSetStoreAlias');

        editButton.classList.add("edit-btn","clear-btn")
        editButton.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" height="16" viewBox="0 0 24 24" width="16" fill="currentColor">
                                    <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM21.41 6.34a1.25 1.25 0 000-1.77l-2-2a1.25 1.25 0 00-1.77 0l-1.83 1.83 3.75 3.75 1.85-1.81z"/>
                                    </svg>`;
        // Кнопка удаления склада
        const removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.classList.add("remove-btn","clear-btn");
        removeButton.textContent = "❌";

        removeButton.addEventListener("click", function () {
            li.remove();
            saveStoreOrderToBackend();
        });

        const hiddenInput = document.createElement("input");
        hiddenInput.type = "hidden";
        hiddenInput.name = "storeIds";
        hiddenInput.value = id;

        li.appendChild(span);
        li.appendChild(editButton);
        li.appendChild(removeButton);
        li.appendChild(hiddenInput);

        selectedStores.appendChild(li);
    }

    Sortable.create(selectedStores, {
        animation: 150,
        onEnd: function () {
            const items = selectedStores.querySelectorAll("li");
            items.forEach(li => {
                const input = li.querySelector("input[name='storeIds']");
                if (input) li.removeChild(input);
            });
            items.forEach(li => {
                const newInput = document.createElement("input");
                newInput.type = "hidden";
                newInput.name = "storeIds";
                newInput.value = li.dataset.id;
                li.appendChild(newInput);
            });
            saveStoreOrderToBackend();
        }
    });

    function saveStoreOrderToBackend() {
        const items = document.querySelectorAll("#selectedStores li");
        const orderedIds = Array.from(items).map(li => li.dataset.id);

        fetch('api/v1/client/store/set-order', {
            method: 'POST',
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
            updateStoreSelectorContainer(data.listDtoNotExistingInStockReport);
            updateSelectedStores(data.listDtoExistingInStockReport);
        })
        .catch(error => {
            console.error("Ошибка при отправке порядка складов:", error);
        });
    }

    function fetchCategories() {
        fetch('api/v1/client/product/category/getAvailable')
            .then(response => response.json())
            .then(data => {
                updateCategorySelector(data.categoriesNotPresent);
                updateSortableCategories(data.categoriesIsPresent);
            })
            .catch(error => console.error("Ошибка загрузки категорий:", error));
    }

    function updateSortableCategories(categories) {
        sortableList.innerHTML = "";
        categories.forEach(cat => {
            const li = document.createElement("li");
            li.className = "sortable-item";
            li.dataset.id = cat.id;
            li.innerHTML = `
                ${cat.name}
                <button type="button" class="remove-btn clear-btn">❌</button>
                <input type="hidden" name="categoryIds" value="${cat.id}" />
            `;
            li.querySelector(".remove-btn").addEventListener("click", function () {
                li.remove();
                saveCategoryOrderToBackend(); // раньше был fetchCategories()
            });
            sortableList.appendChild(li);
        });
    }

    function updateCategorySelector(categories) {
        const container = document.getElementById("categorySelectorContainer");
        container.innerHTML = '';

        if (categories.length === 0) return;

        const select = document.createElement("select");
        select.id = "categorySelector";
        select.className = "form-select";

        const defaultOption = document.createElement("option");
        defaultOption.value = "";
        defaultOption.disabled = true;
        defaultOption.selected = true;
        defaultOption.textContent = "Выберите категорию";
        select.appendChild(defaultOption);

        categories.forEach(category => {
            const option = document.createElement("option");
            option.value = category.id;
            option.textContent = category.name;
            select.appendChild(option);
        });

        select.addEventListener("change", function () {
            const selectedId = this.value;
            const selectedName = this.options[this.selectedIndex].text;

            if (selectedId) {
                addCategoryToSortable(selectedId, selectedName);
                this.selectedIndex = 0;
            }
        });
        container.appendChild(select);
    }

    function addCategoryToSortable(id, name) {
        if (document.querySelector(`#sortableCategories li[data-id="${id}"]`)) {
            return;
        }
        const li = document.createElement("li");
        li.className = "sortable-item";
        li.dataset.id = id;
        li.innerHTML = `
            ${name}
            <button type="button" class="remove-btn clear-btn">❌</button>
            <input type="hidden" name="categoryIds" value="${id}" />
        `;

        li.querySelector(".remove-btn").addEventListener("click", function () {
            li.remove();
            saveCategoryOrderToBackend();
        });
        sortableList.appendChild(li);
        saveCategoryOrderToBackend();
    }

    Sortable.create(sortableList, {
        animation: 150,
        onEnd: function () {
            const items = sortableList.querySelectorAll("li");

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

            saveCategoryOrderToBackend();
        }
    });

    function saveCategoryOrderToBackend() {
        const items = document.querySelectorAll("#sortableCategories li");
        const orderedIds = Array.from(items).map(li => li.dataset.id);

        fetch('api/v1/client/product/category/setOrdered', {
            method: 'POST',
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
                updateCategorySelector(data.categoriesNotPresent);
                updateSortableCategories(data.categoriesIsPresent);
            })
            .catch(error => {
                console.error("Ошибка при отправке порядка категорий:", error);
            });
    }
});
