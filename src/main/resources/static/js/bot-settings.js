document.addEventListener("DOMContentLoaded", function () {
    const sortableList = document.getElementById("sortableCategories");

    fetchCategories();

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
