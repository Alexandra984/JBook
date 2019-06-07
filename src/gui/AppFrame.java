package gui;

import config.Configuration;
import controller.FriendController;
import controller.MessageController;
import controller.UserController;
import model.FriendRequest;
import model.Post;
import model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AppFrame extends JFrame {
    static final int width = 1200;
    static final int height = 800;

    List<User> users = new ArrayList<>();
    Set<Integer> friends = new TreeSet<>();
    Set<Integer> requested = new TreeSet<>();
    List<Post> posts = new ArrayList<>();
    Set<Integer> unread = new TreeSet<>();
    int id;

    AppFrame(String token) {
        UserController userController = new UserController();
        FriendController friendController = new FriendController();
        MessageController messageController = new MessageController();

        JFrame frame = this;
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

        JTextField postText = new JTextField();
        postText.setBounds(400, 700, 200, 35); add(postText);
        JButton postBtn = new JButton("Add");
        postBtn.setBounds(650, 700, 100, 35); add(postBtn);

        JTable reqTable = new JTable();
        Object[] columnsReq = {"id", "Request from"};
        DefaultTableModel reqModel = new DefaultTableModel();
        reqModel.setColumnIdentifiers(columnsReq);
        reqTable.setModel(reqModel);
        reqTable.setBackground(Color.LIGHT_GRAY);
        reqTable.setForeground(Color.black);
        reqTable.setFont(font);
        reqTable.setRowHeight(30);
        JScrollPane reqTableScroll = new JScrollPane(reqTable);
        reqTableScroll.setBounds(850, 100, 300, 150);
        reqTable.getColumnModel().getColumn(0).setMaxWidth(0);
        add(reqTableScroll);

        JButton reqBtn = new JButton("Accept");
        reqBtn.setBounds(950, 250, 100, 35); add(reqBtn);

        JTable peopleTable = new JTable();
        Object[] columnsPeople = {"id", "Person"};
        DefaultTableModel peopleModel = new DefaultTableModel();
        peopleModel.setColumnIdentifiers(columnsPeople);
        peopleTable.setModel(peopleModel);
        peopleTable.setBackground(Color.LIGHT_GRAY);
        peopleTable.setForeground(Color.black);
        peopleTable.setFont(font);
        peopleTable.setRowHeight(30);
        JScrollPane peopleTableScroll = new JScrollPane(peopleTable);
        peopleTableScroll.setBounds(850, 300, 300, 300);
        peopleTable.getColumnModel().getColumn(0).setMaxWidth(0);
        add(peopleTableScroll);

        JButton sendBtn = new JButton("Send req.");
        sendBtn.setBounds(950, 600, 100, 35); add(sendBtn);
        JButton viewBtn = new JButton("View");
        viewBtn.setBounds(950, 650, 100, 35); add(viewBtn);


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
                return userController.getUserData(token);
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
                users = userController.getUsers();
                posts = userController.getPosts(userController.getUserData(token).getId());
                friends.addAll(friendController.getFriends(token).stream().map(User::getId).collect(Collectors.toList()));
                requested.addAll(friendController.getFriendRequests(token)
                        .stream().map(FriendRequest::getFromId).collect(Collectors.toList()));
                requested.addAll(friendController.getSentFriendRequests(token)
                        .stream().map(FriendRequest::getToId).collect(Collectors.toList()));
                unread.addAll(messageController.getUnread(token));
                id = userController.getUserData(token).getId();
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
                    for (User user : users) {
                        if (user.getId() == id)
                            continue;
                        peopleModel.addRow(new Object[]{
                                user.getId(),
                                user.getFirstName() + " " + user.getLastName() +
                                        (friends.contains(user.getId()) ? " [Friend]" : "") +
                                        (unread.contains(user.getId()) ? " *" : "")
                        });
                    }
                    for (Integer r : requested) {
                        User user = userController.getUserById(r);
                        reqModel.addRow(new Object[] {
                                user.getId(),
                                user.getFirstName() + " " + user.getLastName()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                }
            }
        };
        worker2.execute();

        postBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = postText.getText();
                if (content == null || content.equals(""))
                    return;
                postText.setText("");
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        userController.createPost(token, content);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            postModel.addRow(new Object[]{
                                    (new java.sql.Date((new Date()).getTime())).toString() , content
                            });
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(frame, e.getMessage());
                        }
                    }
                };
                worker.execute();
            }
        });

        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = peopleTable.getSelectedRow();
                if (i < 0)
                    return;
                int userId = (Integer) peopleModel.getValueAt(i, 0);
                if (friends.contains(userId) || requested.contains(userId)) {
                    JOptionPane.showMessageDialog(frame, "Already a friend or requested");
                    return;
                }
                try {
                    friendController.sendFriendRequest(token, userId);
                    requested.add(userId);
                    JOptionPane.showMessageDialog(frame, "Request sent");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
            }
        });

        reqBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = reqTable.getSelectedRow();
                if (i < 0)
                    return;
                int userId = (Integer) reqModel.getValueAt(i, 0);
                requested.remove(userId);
                friends.add(userId);
                for (int j = 0; j < peopleTable.getRowCount(); ++j) {
                    if ((Integer) peopleModel.getValueAt(j, 0) == userId)
                        peopleModel.setValueAt(peopleModel.getValueAt(j, 1).toString() + " [Friend]", j, 1);
                }
                reqModel.removeRow(i);
                try {
                    friendController.acceptFriendRequest(token, userId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
            }
        });

        viewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = peopleTable.getSelectedRow();
                if (i < 0)
                    return;
                int userId = (Integer) peopleModel.getValueAt(i, 0);
                if (!friends.contains(userId)) {
                    JOptionPane.showMessageDialog(frame, "Not a friend");
                    return;
                }
                if (unread.contains(userId)) {
                    unread.remove(userId);
                    for  (int j = 0; j < peopleModel.getRowCount(); ++j)
                        if ((Integer) peopleModel.getValueAt(j, 0) == userId)
                            peopleModel.setValueAt(
                                    peopleModel.getValueAt(j, 1).toString()
                                            .substring(0, peopleModel.getValueAt(j, 1).toString().length() - 2),
                                    j, 1);
                }
                FriendFrame friendFrame = new FriendFrame(token, userId);
            }
        });

        setVisible(true);
    }
}
