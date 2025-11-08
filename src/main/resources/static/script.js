document.addEventListener('DOMContentLoaded', () => {

    const searchForm = document.getElementById('searchForm');
    const pokemonInput = document.getElementById('pokemonInput');
    const saveButton = document.getElementById('saveButton');

    const welcomeMessage = document.getElementById('welcomeMessage');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const errorMessage = document.getElementById('errorMessage');
    const pokemonInfoScreen = document.getElementById('pokemonInfoScreen');
    const pokemonImage = document.getElementById('pokemonImage');
    const pokemonNumber = document.getElementById('pokemonNumber');
    const pokemonNameScreen = document.getElementById('pokemonNameScreen');

    const dataDisplay = document.getElementById('dataDisplay');
    const pokemonType = document.getElementById('pokemonType');
    const statsGrid = document.getElementById('statsGrid');
    const evolutionList = document.getElementById('evolutionList');
    const pokemonHeight = document.getElementById('pokemonHeight');
    const pokemonWeight = document.getElementById('pokemonWeight');

    let currentPokemonData = null;

    searchForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const pokemonName = pokemonInput.value.trim();
        if (pokemonName) {
            fetchPokemon(pokemonName);
        }
    });

    async function fetchPokemon(name) {
        showLoading();

        try {
            const response = await fetch(`/pokemon/${name.toLowerCase()}`);

            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('Pokémon não encontrado!');
                }
                throw new Error('Erro ao buscar. Tente novamente.');
            }

            const data = await response.json();
            currentPokemonData = data;
            updateUI(data);
            showData();

        } catch (error) {
            showError(error.message);
        }
    }

    saveButton.addEventListener('click', async () => {
        if (!currentPokemonData) return;

        saveButton.disabled = true;

        try {
            const response = await fetch('/pokemon/salvar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(currentPokemonData),
            });

            if (!response.ok) {
                throw new Error('Falha ao salvar Pokémon.');
            }

            alert(`Pokémon ${currentPokemonData.nome} salvo com sucesso!`);

        } catch (error) {
            alert(error.message);
        } finally {
            saveButton.disabled = false;
        }
    });

    function updateUI(data) {
        pokemonImage.src = data.imagem || '';
        pokemonImage.alt = `Imagem de ${data.nome}`;
        pokemonNumber.textContent = `#${data.id.toString().padStart(3, '0')}`;
        pokemonNameScreen.textContent = data.nome;

        pokemonHeight.textContent = `Altura: ${(data.altura / 10).toFixed(1)} m`;
        pokemonWeight.textContent = `Peso: ${(data.peso / 10).toFixed(1)} kg`;

        pokemonType.textContent = data.tipoPrincipal;
        pokemonType.className = `type-${data.tipoPrincipal.toLowerCase()}`;

        renderStats(data.stats);

        renderEvolutions(data.evolucoes, data.nome);

        saveButton.disabled = false;
    }

    function renderStats(stats) {
        statsGrid.innerHTML = '';
        stats.forEach(stat => {
            const statName = document.createElement('span');
            statName.className = 'stat-name';
            statName.textContent = stat.nome;

            const barContainer = document.createElement('div');
            barContainer.className = 'stat-bar-container';

            const bar = document.createElement('div');
            bar.className = 'stat-bar';

            if (stat.valor < 50) bar.classList.add('low');
            else if (stat.valor < 100) bar.classList.add('medium');
            else bar.classList.add('high');

            const widthPercent = (stat.valor / 255) * 100;
            bar.style.width = `${widthPercent > 100 ? 100 : widthPercent}%`;

            barContainer.appendChild(bar);
            statsGrid.appendChild(statName);
            statsGrid.appendChild(barContainer);
        });
    }

    function renderEvolutions(evolutions, currentName) {
        evolutionList.innerHTML = '';

        const otherEvolutions = evolutions ? evolutions.filter(evoName => evoName !== currentName) : [];

        if (otherEvolutions.length === 0) {
            evolutionList.innerHTML = '<span class="evolution-item">Sem evoluções</span>';
            return;
        }

        otherEvolutions.forEach(evoName => {
            const evoItem = document.createElement('span');
            evoItem.className = 'evolution-item';
            evoItem.textContent = evoName;
            evoItem.dataset.name = evoName;
            evolutionList.appendChild(evoItem);
        });
    }

    evolutionList.addEventListener('click', (e) => {
        if (e.target.classList.contains('evolution-item') && e.target.dataset.name) {
            const name = e.target.dataset.name;
            pokemonInput.value = name;
            fetchPokemon(name);
        }
    });

    function showLoading() {
        welcomeMessage.classList.add('hidden');
        errorMessage.classList.add('hidden');
        pokemonInfoScreen.classList.add('hidden');
        dataDisplay.classList.add('hidden');
        loadingIndicator.classList.remove('hidden');
        saveButton.disabled = true;
        currentPokemonData = null;
    }

    function showError(message) {
        welcomeMessage.classList.add('hidden');
        loadingIndicator.classList.add('hidden');
        pokemonInfoScreen.classList.add('hidden');
        dataDisplay.classList.add('hidden');

        errorMessage.textContent = message;
        errorMessage.classList.remove('hidden');
        saveButton.disabled = true;
    }

    function showData() {
        welcomeMessage.classList.add('hidden');
        loadingIndicator.classList.add('hidden');
        errorMessage.classList.add('hidden');

        pokemonInfoScreen.classList.remove('hidden');
        dataDisplay.classList.remove('hidden');
    }
});