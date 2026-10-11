let loginHTML = ''; // Biến để lưu trữ lại giao diện form đăng nhập

// Lấy giao diện gốc lúc trang vừa load xong
document.addEventListener("DOMContentLoaded", () => {
    const rightPanel = document.getElementById('rightPanel');
    loginHTML = rightPanel.innerHTML;
});

// Hàm gọi API fetch để tải html của form đăng ký đắp vào cột phải
function loadRegister(event) {
    event.preventDefault(); // Chặn hành vi load lại trang của thẻ <a>
    fetch('register.html')
        .then(response => {
            if (!response.ok) throw new Error("Cần chạy qua Local Server để dùng fetch");
            return response.text();
        })
        .then(html => {
            document.getElementById('rightPanel').innerHTML = html;
        })
        .catch(err => {
            alert("Lỗi: Bạn đang chạy file trực tiếp bằng trình duyệt. Hãy dùng Live Server trên VS Code để dùng lệnh fetch.");
            console.error(err);
        });
}

// Hàm khôi phục lại html của form đăng nhập từ biến đã lưu
function loadLogin(event) {
    event.preventDefault();
    document.getElementById('rightPanel').innerHTML = loginHTML;
}

// Hàm ẩn hiện mật khẩu (Dùng chung)
function togglePassword(inputId, iconElement) {
    const input = document.getElementById(inputId);
    if (input.type === "password") {
        input.type = "text";
        iconElement.textContent = "🙈";
    } else {
        input.type = "password";
        iconElement.textContent = "👁️";
    }
}

// Sử dụng Event Delegation để bắt sự kiện click cho các nút vì form bị thay đổi liên tục
document.addEventListener('click', function(e) {
    // Bắt sự kiện ấn nút Đăng nhập
    if (e.target && e.target.id === 'btnLogin') {
        const email = document.getElementById('email').value;
        if(email.trim() === '') {
            alert("Vui lòng nhập Email hoặc Số điện thoại!");
        } else {
            alert("Đang kết nối Server với tài khoản: " + email);
        }
    }
    // Bắt sự kiện ấn nút Đăng ký
    else if (e.target && e.target.id === 'btnRegister') {
        const pwd = document.getElementById('reg-password').value;
        const confirm = document.getElementById('reg-confirm').value;

        if (pwd !== confirm) {
            alert("Mật khẩu xác nhận không khớp!");
        } else {
            alert("Đang xử lý đăng ký...");
        }
    }
});