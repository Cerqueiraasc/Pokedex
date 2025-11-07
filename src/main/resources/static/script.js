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

    const typeClasses = [
        'type-normal', 'type-fire', 'type-water', 'type-electric', 'type-grass',
        'type-ice', 'type-fighting', 'type-poison', 'type-ground', 'type-flying',
        'type-psychic', 'type-bug', 'type-rock', 'type-ghost', 'type-dragon',
        'type-dark', 'type-steel', 'type-fairy', 'type-desconhecido'
    ];

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
        // --- CORREÇÃO PRINCIPAL AQUI ---
        // O JSON do backend usa 'tipo_principal' (snake_case)
        const { nome, id, tipo_principal, imagem } = data;

        pokemonImage.src = imagem || '';
        pokemonImage.alt = nome;
        pokemonNumber.textContent = `#${String(id).padStart(3, '0')}`;
        pokemonNameScreen.textContent = nome;

        pokemonType.textContent = tipo_principal || 'N/A';
        applyTypeStyle(pokemonType, tipo_principal || 'desconhecido');
        typeContainer.classList.remove("hidden");
        // --- FIM DA CORREÇÃO ---

        pokemonInfo.classList.remove("hidden");
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
        applyTypeStyle(pokemonType, null);
    }

    function applyTypeStyle(element, type) {
        typeClasses.forEach(c => element.classList.remove(c));

        if (type) {
            element.classList.add(`type-${type}`);
        }
    }
});