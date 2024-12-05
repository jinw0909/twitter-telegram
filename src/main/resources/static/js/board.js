window.onload = function() {
    // Fetch all user divs (with classes as the user id)
    const users = document.querySelectorAll('.user');

    // Fetch all tweet divs (with classes as the author id)
    const tweets = document.querySelectorAll('.tweet');
    const percentage = (100 / users.length - 5);

    // Iterate through all users and apply styles to matching tweet divs
    users.forEach((user, index) => {
        const userId = user.classList[0];  // Directly use user.id as class

        // Apply text-align style based on the index

        // Apply a common style to all tweets authored by this user
        tweets.forEach(tweet => {
            if (tweet.classList.contains(userId)) {
                // Apply text-align style based on index
                tweet.style.order = index;
                tweet.style.border = '1px solid black';  // Example: border for tweet
                // Apply the calculated percentage as the width
                tweet.style.width = `${percentage}%`;
            }
        });
    });
};
