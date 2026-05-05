const allCards = document.querySelectorAll('.room-card');
document.querySelectorAll('.filter').forEach(button => {
    button.addEventListener('click', () => {
        document.querySelectorAll('.filter').forEach(b => b.classList.remove('active'));
        button.classList.add('active');
        const type = button.dataset.type;
        allCards.forEach(card => {
            card.style.display = (type === 'all' || card.dataset.type === type) ? '' : 'none';
        });
    });
});