const mainContainer = document.getElementById('mainContainer');
const toLogin = document.getElementById('toLogin');
const toRegister = document.getElementById('toRegister');
const avatarImg = document.getElementById('avatarImg');

// 两张图片地址
const boyImg = "image/login.png";
const girlImg = "image/register.png";

toLogin.addEventListener('click', () => {
    mainContainer.classList.remove('show-register');
    avatarImg.src = boyImg;
});
toRegister.addEventListener('click', () => {
    mainContainer.classList.add('show-register');
    avatarImg.src = girlImg;
});

