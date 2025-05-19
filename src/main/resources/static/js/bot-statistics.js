document.addEventListener("DOMContentLoaded", function () {

    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0'); // месяцы с 0
    const dd = String(today.getDate()).padStart(2, '0');

    // Устанавливаю сегодня дату при загрузке страницы
    const formattedDate = `${yyyy}-${mm}-${dd}`;
    document.getElementById("date-current-start").value = formattedDate;
    document.getElementById("date-current-end").value = formattedDate;

    fetchStatisticsByPeriod();

    // Слушатели изменений периода дат
    document.getElementById("date-current-start").addEventListener("change", fetchStatisticsByPeriod);
    document.getElementById("date-current-end").addEventListener("change", fetchStatisticsByPeriod);

    function fetchStatisticsByPeriod() {
    const dateStart = document.getElementById("date-current-start").value;
    const dateEnd = document.getElementById("date-current-end").value
    fetch(`/api/v1/bot/statistics/by-period?start=${dateStart}&end=${dateEnd}`)
            .then(response => response.json())
            .then(data => updateStatistics(data))
            .catch(error => console.error("Ошибка загрузки статистики за период:", error));
    }

    function updateStatistics(data) {
        document.getElementById("newBotUsers").textContent = data.countRegistration;
        document.getElementById("countClickAction").textContent = data.countClickAction;
        document.getElementById("countClickNextAction").textContent = data.countClickNextAction;
        document.getElementById("countClickPhoto").textContent = data.countClickPhoto;
        document.getElementById("countClickSearch").textContent = data.countSearchRequest;
        document.getElementById("countClickQuestion").textContent = data.countCreateQuestions;
    }

});