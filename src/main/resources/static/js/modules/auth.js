import { api } from '../api.js';
import { qs, qsa, formDataObject, toast, humanizeError, setButtonLoading, renderIcons } from '../utils.js';
import { saveSession, loadCurrentUser, loadCustomerCollections, isAdmin } from '../state.js';

function bindPasswordToggles(root) {
    qsa('[data-toggle-password]', root).forEach((button) => {
        button.addEventListener('click', () => {
            const input = button.parentElement.querySelector('input');
            input.type = input.type === 'password' ? 'text' : 'password';
            button.dataset.icon = input.type === 'password' ? 'eye' : 'x';
            button.dataset.iconReady = 'false';
            renderIcons(button);
        });
    });
}

export async function renderLogin({ root, refreshShell }) {
    bindPasswordToggles(root);
    const form = qs('[data-role="login-form"]', root);
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!form.reportValidity()) return;
        const button = qs('button[type="submit"]', form);
        const data = formDataObject(form);
        setButtonLoading(button, true, 'Đang đăng nhập...');
        try {
            const response = await api.login(data.email.trim(), data.password);
            saveSession({ idUser: response.idUser, email: response.email });
            await loadCurrentUser(true);
            if (!isAdmin()) await loadCustomerCollections(true);
            await refreshShell();
            toast('Chào mừng bạn quay lại Petshop.');
            const redirect = sessionStorage.getItem('pawcare.redirect');
            sessionStorage.removeItem('pawcare.redirect');
            location.hash = isAdmin() ? '#/admin' : (redirect || '#/home');
        } catch (error) {
            toast(humanizeError(error), 'error', 'Đăng nhập chưa thành công');
            setButtonLoading(button, false);
        }
    });
}

export async function renderRegister({ root }) {
    const form = qs('[data-role="register-form"]', root);
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!form.reportValidity()) return;
        const button = qs('button[type="submit"]', form);
        const data = formDataObject(form);
        setButtonLoading(button, true, 'Đang tạo tài khoản...');
        try {
            await api.register({
                name: data.name.trim(),
                email: data.email.trim(),
                password: data.password,
                addressOrder: data.addressOrder.trim(),
                phoneOrder: data.phoneOrder.trim()
            });
            toast('Tài khoản đã được tạo. Bạn có thể đăng nhập ngay.');
            location.hash = '#/login';
        } catch (error) {
            toast(humanizeError(error), 'error', 'Chưa thể đăng ký');
            setButtonLoading(button, false);
        }
    });
}
