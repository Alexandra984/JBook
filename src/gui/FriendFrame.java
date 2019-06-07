package gui;

import config.Configuration;
import controller.FriendController;
import controller.MessageController;
import controller.UserController;
import model.FriendRequest;
import model.Message;
import model.Post;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FriendFrame extends JFrame {
    static final int width = 1200;
    static final int height = 800;

    List<Post> posts = new ArrayList<>();
    List<Message> messages = new ArrayList<>();

    FriendFrame(String token, int friendId) {
        UserController userController = new UserController();
        MessageController messageController = new MessageController();

        JFrame frame = this;
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTable postTable = new JTable();
        Object[] columnsPost = {"date", "content"};
        DefaultTableModel postModel = new DefaultTableModel();
        postModel.setColumnIdentifiers(columnsPost);
        postTable.setModel(postModel);
        postTable.setBackground(Color.LIGHT_GRAY);
        postTable.setForeground(Color.black);
        Font font = new Font("",1,10);
        postTable.setFont(font);
        postTable.setRowHeight(30);
        JScrollPane postTableScroll = new JScrollPane(postTable);
        postTableScroll.setBounds(400, 100, 350, 550);
        postTable.getColumnModel().getColumn(0).setMaxWidth(100);
        add(postTableScroll);

        JTable msgTable = new JTable();
        Object[] columnsMsg = {"from", "message"};
        DefaultTableModel msgModel = new DefaultTableModel();
        msgModel.setColumnIdentifiers(columnsMsg);
        msgTable.setModel(msgModel);
        msgTable.setBackground(Color.LIGHT_GRAY);
        msgTable.setForeground(Color.black);
        msgTable.setFont(font);
        msgTable.setRowHeight(30);
        JScrollPane msgTableScroll = new JScrollPane(msgTable);
        msgTableScroll.setBounds(850, 100, 300, 550);
        msgTable.getColumnModel().getColumn(0).setMaxWidth(100);
        add(msgTableScroll);

        JTextField msgText = new JTextField();
        msgText.setBounds(850, 700, 200, 35); add(msgText);
        JButton msgBtn = new JButton("Send");
        msgBtn.setBounds(1050, 700, 100, 35); add(msgBtn);

        JLabel nameText = new JLabel();
        JLabel bioText = new JLabel();
        bioText.setBounds(60, 150, 300, 35); frame.add(bioText);
        nameText.setBounds(60, 100, 100, 35); frame.add(nameText);
        JLabel imageLabel = new JLabel();
        imageLabel.setBounds(520, 30, 0, 0);
        add(imageLabel);

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userController.getUserById(friendId);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    String image;
                    if (user.getProfileImagePath() == null)
                        image = Configuration.DEFAULT_PROFILE_IMAGE;
                    else
                        image = user.getProfileImagePath();
                    imageLabel.setIcon(new ImageIcon(image));
                    nameText.setText(user.getFirstName() + " " + user.getLastName());
                    bioText.setText(user.getBio());

                    frame.repaint();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                }
            }
        };
        worker.execute();

        SwingWorker<Void, Void> worker2 = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                posts = userController.getPosts(friendId);
                messageController.setRead(token, friendId);
                messages = messageController.getMessages(token, friendId);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    for (Post post : posts) {
                        postModel.addRow(new Object[]{
                                post.getDate(), post.getContent()
                        });
                    }
                    for (Message message : messages) {
                        msgModel.insertRow(0, new Object[] {
                                userController.getUserById(message.getFromId()).getFirstName(),
                                message.getContent()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                }
            }
        };
        worker2.execute();

        msgBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = msgText.getText();
                if (message == null || message.equals(""))
                    return;
                try {
                    msgText.setText("");
                    messageController.sendMessage(token, friendId, message);
                    msgModel.addRow(new Object[] {
                            userController.getUserData(token).getFirstName(),
                            message
                    });
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
            }
        });

        setVisible(true);
    }
}
