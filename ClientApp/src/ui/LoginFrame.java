package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkRemember;

    public LoginFrame() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Đăng nhập - Bếp Nhà Restaurant");
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(1, 2));

        add(createLeftPanel());
        add(createRightPanel());
    }

    // Hàm tiện ích load font (Chỉ dùng Be Vietnam Pro)
    private Font getCustomFont(int style, int size) {
        return new Font("Be Vietnam Pro", style, size);
    }

    // ================= PHẦN BÊN TRÁI =================
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(32, 43, 37));
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // 1. Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setOpaque(false);

        JLabel lblIcon = new JLabel("⌂ ");
        lblIcon.setFont(getCustomFont(Font.BOLD, 30));
        lblIcon.setForeground(new Color(206, 93, 52));

        JPanel logoTextPanel = new JPanel();
        logoTextPanel.setLayout(new BoxLayout(logoTextPanel, BoxLayout.Y_AXIS));
        logoTextPanel.setOpaque(false);

        JLabel lblBrandName = new JLabel("Bếp Nhà");
        lblBrandName.setFont(getCustomFont(Font.BOLD, 18));
        lblBrandName.setForeground(Color.WHITE);
        lblBrandName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBrandSub = new JLabel("RESTAURANT");
        lblBrandSub.setFont(getCustomFont(Font.BOLD, 9));
        lblBrandSub.setForeground(new Color(140, 150, 145));
        lblBrandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoTextPanel.add(lblBrandName);
        logoTextPanel.add(lblBrandSub);

        logoPanel.add(lblIcon);
        logoPanel.add(logoTextPanel);
        leftPanel.add(logoPanel, BorderLayout.NORTH);

        // 2. Nội dung giữa
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        JLabel lblBadge = new JLabel("VẬN HÀNH NHÀ HÀNG, ĐƠN GIẢN HƠN");
        lblBadge.setFont(getCustomFont(Font.BOLD, 10));
        lblBadge.setForeground(new Color(201, 142, 84));
        lblBadge.setOpaque(true);
        lblBadge.setBackground(Color.WHITE);
        lblBadge.setBorder(new EmptyBorder(6, 12, 6, 12));
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle1 = new JLabel("Mỗi đơn món,");
        lblTitle1.setFont(getCustomFont(Font.BOLD, 34));
        lblTitle1.setForeground(Color.WHITE);
        lblTitle1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle2 = new JLabel("một trải nghiệm");
        lblTitle2.setFont(getCustomFont(Font.BOLD, 34));
        lblTitle2.setForeground(Color.WHITE);
        lblTitle2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle3 = new JLabel("trọn vẹn.");
        lblTitle3.setFont(getCustomFont(Font.BOLD, 34));
        lblTitle3.setForeground(new Color(201, 142, 84));
        lblTitle3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc1 = new JLabel("Kết nối khách hàng, bếp và đội ngũ phục vụ trong một hệ");
        lblDesc1.setFont(getCustomFont(Font.PLAIN, 13));
        lblDesc1.setForeground(new Color(152, 161, 157));
        lblDesc1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc2 = new JLabel("thống thời gian thực.");
        lblDesc2.setFont(getCustomFont(Font.PLAIN, 13));
        lblDesc2.setForeground(new Color(152, 161, 157));
        lblDesc2.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerContent.add(lblBadge);
        centerContent.add(Box.createVerticalStrut(20));
        centerContent.add(lblTitle1);
        centerContent.add(lblTitle2);
        centerContent.add(lblTitle3);
        centerContent.add(Box.createVerticalStrut(20));
        centerContent.add(lblDesc1);
        centerContent.add(Box.createVerticalStrut(5));
        centerContent.add(lblDesc2);

        centerWrapper.add(centerContent);
        leftPanel.add(centerWrapper, BorderLayout.CENTER);

        return leftPanel;
    }

    // ================= PHẦN BÊN PHẢI =================
    private JPanel createRightPanel() {
        // Sử dụng GridBagLayout làm Wrapper ngoài cùng để CĂN GIỮA tuyệt đối
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setBackground(Color.WHITE);

        // Container chứa Form (Cố định chiều rộng để form không bị giãn quá mức)
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setPreferredSize(new Dimension(380, 480));
        formContainer.setMinimumSize(new Dimension(380, 480));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Chào mừng trở lại");
        lblTitle.setFont(getCustomFont(Font.BOLD, 28));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Đăng nhập để tiếp tục vào hệ thống Bếp Nhà");
        lblSubtitle.setFont(getCustomFont(Font.PLAIN, 13));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Form Email
        JLabel lblEmail = new JLabel("Email hoặc số điện thoại");
        lblEmail.setFont(getCustomFont(Font.BOLD, 12));
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtEmail = new JTextField();
        txtEmail.setFont(getCustomFont(Font.PLAIN, 13));
        txtEmail.setMaximumSize(new Dimension(380, 40)); // Khóa kích thước
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        styleTextField(txtEmail);

        // Form Mật khẩu
        JLabel lblPassword = new JLabel("Mật khẩu");
        lblPassword.setFont(getCustomFont(Font.BOLD, 12));
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setFont(getCustomFont(Font.PLAIN, 13));
        txtPassword.setMaximumSize(new Dimension(380, 40));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        styleTextField(txtPassword);

        // Tùy chọn
        JPanel pnlOptions = new JPanel(new BorderLayout());
        pnlOptions.setBackground(Color.WHITE);
        pnlOptions.setMaximumSize(new Dimension(380, 30));
        pnlOptions.setAlignmentX(Component.LEFT_ALIGNMENT);

        chkRemember = new JCheckBox("Ghi nhớ đăng nhập");
        chkRemember.setBackground(Color.WHITE);
        chkRemember.setFont(getCustomFont(Font.PLAIN, 12));
        chkRemember.setFocusPainted(false);

        JLabel lblForgot = new JLabel("Quên mật khẩu?");
        lblForgot.setFont(getCustomFont(Font.BOLD, 12));
        lblForgot.setForeground(new Color(206, 93, 52));
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlOptions.add(chkRemember, BorderLayout.WEST);
        pnlOptions.add(lblForgot, BorderLayout.EAST);

        // Nút Đăng nhập
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setMaximumSize(new Dimension(380, 45));
        btnLogin.setBackground(new Color(206, 93, 52));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(getCustomFont(Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnLogin.setBackground(new Color(181, 79, 44));
            }
            public void mouseExited(MouseEvent evt) {
                btnLogin.setBackground(new Color(206, 93, 52));
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtEmail.getText();
                JOptionPane.showMessageDialog(LoginFrame.this, "Đang kết nối Server với tài khoản: " + username);
            }
        });

        // Link Đăng ký
        JPanel pnlRegister = new JPanel();
        pnlRegister.setLayout(new BoxLayout(pnlRegister, BoxLayout.X_AXIS));
        pnlRegister.setBackground(Color.WHITE);
        pnlRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNoAccount = new JLabel("Chưa có tài khoản? ");
        lblNoAccount.setFont(getCustomFont(Font.PLAIN, 13));
        lblNoAccount.setForeground(Color.GRAY);

        JLabel lblRegLink = new JLabel("Đăng ký ngay");
        lblRegLink.setFont(getCustomFont(Font.BOLD, 13));
        lblRegLink.setForeground(new Color(206, 93, 52));
        lblRegLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlRegister.add(lblNoAccount);
        pnlRegister.add(lblRegLink);

        // Lắp ráp Form
        formContainer.add(lblTitle);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(lblSubtitle);
        formContainer.add(Box.createVerticalStrut(40));

        formContainer.add(lblEmail);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(txtEmail);
        formContainer.add(Box.createVerticalStrut(20));

        formContainer.add(lblPassword);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(txtPassword);
        formContainer.add(Box.createVerticalStrut(10));

        formContainer.add(pnlOptions);
        formContainer.add(Box.createVerticalStrut(30));

        formContainer.add(btnLogin);
        formContainer.add(Box.createVerticalStrut(30));

        formContainer.add(pnlRegister);

        // Đưa formContainer vào Wrapper căn giữa
        rightWrapper.add(formContainer);

        return rightWrapper;
    }

    private void styleTextField(JTextField textField) {
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }
}