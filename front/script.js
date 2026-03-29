async function submitIdea() 
{
    const statement=document.getElementById('ideaInput').value;
    if (!statement) return alert("Please enter an idea");
    await fetch('/api/ideas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ statement })
    });
    
    document.getElementById('ideaInput').value = '';
    loadIdeas();
}

async function loadIdeas() {
    const res = await fetch('/api/ideas');
    const ideas = await res.json();
    const list = document.getElementById('ideasList');
    list.innerHTML = '';

    ideas.forEach(idea => {
        list.innerHTML += `
            <div class="idea-card ${idea.category.toLowerCase()}">
                <p>${idea.statement}</p>
                <span>Status: ${idea.category} | Votes: ${idea.votes}</span>
                <button onclick="vote(${idea.id})">👍</button>
                <button onclick="deleteIdea(${idea.id})">🗑️</button>
            </div>
        `;
    });
    updateChart(ideas);
}

async function vote(id) {
    await fetch(`/api/ideas/${id}/vote`, { method: 'PUT' });
    loadIdeas();
}

async function deleteIdea(id) {
    await fetch(`/api/ideas/${id}`, { method: 'DELETE' });
    loadIdeas();
}

function updateChart(ideas) {
    const pos = ideas.filter(i => i.category === 'Positive').length;
    const neg = ideas.filter(i => i.category === 'Negative').length;
    const neu = ideas.filter(i => i.category === 'Neutral').length;

    new Chart(document.getElementById('sentimentChart'), {
        type: 'pie',
        data: {
            labels: ['Positive', 'Negative', 'Neutral'],
            datasets: [{ data: [pos, neg, neu], backgroundColor: ['#2ecc71', '#e74c3c', '#95a5a6'] }]
        }
    });
}

loadIdeas();
