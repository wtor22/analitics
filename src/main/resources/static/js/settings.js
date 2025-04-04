document.addEventListener("DOMContentLoaded", function () {
    const formDemandsUpdater = document.getElementById("demands-updater");
    const dateFromInput = document.getElementById("date-demand-start");
    const dateToInput = document.getElementById("date-demand-end");
    const stringResponse = document.getElementById("demands-updater-response");

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