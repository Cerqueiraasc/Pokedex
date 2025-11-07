document.addEventListener("DOMContentLoaded", () => {

    const searchButton = document.getElementById("searchButton");
    const pokemonInput = document.getElementById("pokemonInput");

    const welcomeMessage = document.getElementById("welcomeMessage");
    const loadingIndicator = document.getElementById("loadingIndicator");
    const pokemonInfo = document.getElementById("pokemonInfo");
    const errorMessage = document.getElementById("errorMessage");

    const pokemonImage = document.getElementById("pokemonImage");
    const pokemonNumber = document.getElementById("pokemonNumber");
    const pokemonNameScreen = document.getElementById("pokemonNameScreen");

    const typeContainer = document.getElementById("typeContainer");
    const pokemonType = document.getElementById("pokemonType");

    const statsGrid = document.getElementById("statsGrid");
    const heightWeightContainer = document.getElementById("heightWeightContainer");
    const pokemonHeight = document.getElementById("pokemonHeight");
    const pokemonWeight = document.getElementById("pokemonWeight");

    const evolutionContainer = document.getElementById("evolutionContainer");
    const evolutionList = document.getElementById("evolutionList");

    const typeClasses = [
        'type-normal', 'type-fire', 'type-water', 'type-electric', 'type-grass',
        'type-ice', 'type-fighting', 'type-poison', 'type-ground', 'type-flying',
        'type-psychic', 'type-bug', 'type-rock', 'type-ghost', 'type-dragon',
        'type-dark', 'type-steel', 'type-fairy', 'type-desconhecido'
    ];

    const MAX_STAT_VALUE = 255;

    searchButton.addEventListener("click", fetchPokemon);

    pokemonInput.addEventListener("keyup", (event) => {
        if (event.key === "Enter") {
            fetchPokemon();
        }
    });

    async function fetchPokemon() {
        const name = pokemonInput.value.toLowerCase().trim();

        if (!name) {
            showError("Digite um nome ou ID.");
            return;
        }

        hideAll();
        loadingIndicator.classList.remove("hidden");

        try {
            const response = await fetch(`/pokemon/${name}`);

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `Erro ${response.status}`);
            }

            const data = await response.json();
            showPokemon(data);

        } catch (error) {
            showError(error.message);
        } finally {
            loadingIndicator.classList.add("hidden");
        }
    }

    function showPokemon(data) {
        const { nome, id, tipo_principal, imagem, altura, peso, stats, evolucoes } = data;

        pokemonImage.src = imagem || '';
        pokemonImage.alt = nome;
        pokemonNumber.textContent = `#${String(id).padStart(3, '0')}`;
        pokemonNameScreen.textContent = nome;

        pokemonType.textContent = tipo_principal || 'N/A';
        applyTypeStyle(pokemonType, tipo_principal || 'desconhecido');
        typeContainer.classList.remove("hidden");

        pokemonHeight.textContent = altura ? `${(altura / 10.0).toFixed(1)} M` : '---';
        pokemonWeight.textContent = peso ? `${(peso / 10.0).toFixed(1)} KG` : '---';
        heightWeightContainer.classList.remove("hidden");

        statsGrid.innerHTML = '';
        if (stats && stats.length > 0) {
            stats.forEach(stat => {
                const statRow = createStatElement(stat.nome, stat.valor);
                statsGrid.appendChild(statRow);
            });
            statsGrid.classList.remove("hidden");
        }

        evolutionList.innerHTML = '';
        if (evolucoes && evolucoes.length > 1) {
            evolucoes
                .filter(evoName => evoName !== nome)
                .forEach(evoName => {
                    const evoItem = document.createElement('div');
                    evoItem.className = 'evolution-item';
                    evoItem.textContent = evoName;

                    evoItem.addEventListener('click', () => {
                        pokemonInput.value = evoName;
                        fetchPokemon();
                    });

                    evolutionList.appendChild(evoItem);
                });

            if (evolutionList.children.length > 0) {
                evolutionContainer.classList.remove("hidden");
            }
        }

        pokemonInfo.classList.remove("hidden");
    }

    function createStatElement(name, value) {
        const statName = document.createElement('span');
        statName.className = 'stat-name';
        statName.textContent = name;

        const statBarContainer = document.createElement('div');
        statBarContainer.className = 'stat-bar-container';

        const statBar = document.createElement('div');
        statBar.className = 'stat-bar';

        const percentage = (value / MAX_STAT_VALUE) * 100;
        setTimeout(() => {
            statBar.style.width = `${Math.min(percentage, 100)}%`;
        }, 100);

        if (percentage < 33) statBar.classList.add('low');
        else if (percentage < 66) statBar.classList.add('medium');
        else statBar.classList.add('high');

        statBarContainer.appendChild(statBar);

        const fragment = document.createDocumentFragment();
        fragment.appendChild(statName);
        fragment.appendChild(statBarContainer);

        return fragment;
    }

    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.classList.remove("hidden");
    }

    function hideAll() {
        welcomeMessage.classList.add("hidden");
        errorMessage.classList.add("hidden");
        pokemonInfo.classList.add("hidden");
        typeContainer.classList.add("hidden");
        heightWeightContainer.classList.add("hidden");
        statsGrid.classList.add("hidden");
        statsGrid.innerHTML = '';
        evolutionContainer.classList.add("hidden");
        evolutionList.innerHTML = '';
        applyTypeStyle(pokemonType, null);
    }

    function applyTypeStyle(element, type) {
        typeClasses.forEach(c => element.classList.remove(c));

        if (type) {
            element.classList.add(`type-${type}`);
        }
    }
});