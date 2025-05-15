package GUI3;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class EventManagementSystem extends JFrame {
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private User currentUser;

	public EventManagementSystem() {
		setTitle("Event Management System");
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setLayout(new BorderLayout());

		// Create side navigation panel
		JPanel navPanel = createNavPanel();

		// Create main panel
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);

		// Initialize different pages
		mainPanel.add(createBrowsePanel(), "Browse");
		mainPanel.add(createManagePanel(), "Manage");
		mainPanel.add(createBookingPanel(), "Book");
		mainPanel.add(createCancelPanel(), "Cancel");

		// Layout settings
		add(navPanel, BorderLayout.WEST);
		add(mainPanel, BorderLayout.CENTER);
	}

	private JPanel createNavPanel() {
		JPanel navPanel = new JPanel(new GridLayout(8, 1, 5, 5));
		navPanel.setBackground(new Color(50, 50, 50));

		JButton btnBrowseEvents = new JButton("Browse Events");
		JButton btnManageEvents = new JButton("Manage Events");
		JButton btnBookTicket = new JButton("Book Ticket");
		JButton btnCancelBooking = new JButton("Cancel Booking");

		btnBrowseEvents.addActionListener(e -> cardLayout.show(mainPanel, "Browse"));
		btnManageEvents.addActionListener(e -> {
			if (currentUser instanceof Organizer) {
				cardLayout.show(mainPanel, "Manage");
			} else {
				JOptionPane.showMessageDialog(this, "You do not have permission to manage events.", "Permission Denied",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		btnBookTicket.addActionListener(e -> cardLayout.show(mainPanel, "Book"));
		btnCancelBooking.addActionListener(e -> cardLayout.show(mainPanel, "Cancel"));

		navPanel.add(btnBrowseEvents);
		navPanel.add(btnManageEvents);
		navPanel.add(btnBookTicket);
		navPanel.add(btnCancelBooking);

		return navPanel;
	}

	private JPanel createBrowsePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("Browse Events", JLabel.CENTER), BorderLayout.NORTH);
		panel.add(loadAndDisplayEvents(false, false, false), BorderLayout.CENTER);
		return panel;
	}

	private JPanel createBookingPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("Book Tickets", JLabel.CENTER), BorderLayout.NORTH);
		panel.add(loadAndDisplayEvents(true, false, false), BorderLayout.CENTER);
		return panel;
	}

	private JPanel createCancelPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("Cancel Bookings", JLabel.CENTER), BorderLayout.NORTH);
		panel.add(loadAndDisplayEvents(false, true, false), BorderLayout.CENTER);
		return panel;
	}

	// 修复 createManagePanel 方法，恢复 organizerdemanage 功能
	private JPanel createManagePanel() {
		JPanel panel = new JPanel(new BorderLayout());

		JLabel label = new JLabel("Manage Events", JLabel.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 24));
		panel.add(label, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		JButton btnAdd = new JButton("Add Event");
		JButton btnUpdate = new JButton("Update Event");
		JButton btnDelete = new JButton("Delete Event");

		btnAdd.addActionListener(e -> handleAddEvent());
		btnUpdate.addActionListener(e -> handleUpdateEvent());
		btnDelete.addActionListener(e -> handleDeleteEvent());

		buttonPanel.add(btnAdd);
		buttonPanel.add(btnUpdate);
		buttonPanel.add(btnDelete);

		panel.add(buttonPanel, BorderLayout.CENTER);
		return panel;
	}

	// 修复 handleAddEvent 方法，调用 EventDialog
	private void handleAddEvent() {
		EventDialog dialog = new EventDialog();
		Event newEvent = dialog.showDialog();

		if (newEvent != null) {
			Organizer organizer = (Organizer) currentUser;
			organizer.addEvent(newEvent);

			JOptionPane.showMessageDialog(this, "Event added successfully.", "Success",
					JOptionPane.INFORMATION_MESSAGE);
			refreshUI();
		}
	}

	// 修复 handleUpdateEvent 方法
	private void handleUpdateEvent() {
		JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
		JTextField txtOldName = new JTextField();
		JTextField txtOldDate = new JTextField();
		JTextField txtOldLocation = new JTextField();
		JTextField txtNewName = new JTextField();
		JTextField txtNewArtist = new JTextField();
		JTextField txtNewDate = new JTextField();
		JTextField txtNewLocation = new JTextField();
		JTextField txtNewRegularTickets = new JTextField();
		JTextField txtNewVipTickets = new JTextField();

		panel.add(new JLabel("Old Event Name:"));
		panel.add(txtOldName);
		panel.add(new JLabel("Old Event Date:"));
		panel.add(txtOldDate);
		panel.add(new JLabel("Old Event Location:"));
		panel.add(txtOldLocation);
		panel.add(new JLabel("New Event Name:"));
		panel.add(txtNewName);
		panel.add(new JLabel("New Artist:"));
		panel.add(txtNewArtist);
		panel.add(new JLabel("New Date:"));
		panel.add(txtNewDate);
		panel.add(new JLabel("New Location:"));
		panel.add(txtNewLocation);
		panel.add(new JLabel("New Regular Tickets:"));
		panel.add(txtNewRegularTickets);
		panel.add(new JLabel("New VIP Tickets:"));
		panel.add(txtNewVipTickets);

		int result = JOptionPane.showConfirmDialog(this, panel, "Update Event", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				Event updatedEvent = new Event(txtNewName.getText(), txtNewArtist.getText(), txtNewDate.getText(),
						Integer.parseInt(txtNewRegularTickets.getText()), Integer.parseInt(txtNewVipTickets.getText()),
						txtNewLocation.getText());
				Organizer organizer = (Organizer) currentUser;
				organizer.updateEvent(updatedEvent);

				JOptionPane.showMessageDialog(this, "Event updated successfully.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
				refreshUI();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error updating event: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// 修复 handleDeleteEvent 方法
	private void handleDeleteEvent() {
		JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
		JTextField txtName = new JTextField();
		JTextField txtDate = new JTextField();
		JTextField txtLocation = new JTextField();

		panel.add(new JLabel("Event Name:"));
		panel.add(txtName);
		panel.add(new JLabel("Event Date:"));
		panel.add(txtDate);
		panel.add(new JLabel("Event Location:"));
		panel.add(txtLocation);

		int result = JOptionPane.showConfirmDialog(this, panel, "Delete Event", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				Organizer organizer = (Organizer) currentUser;
				organizer.deleteEvent(txtName.getText(), txtDate.getText(), txtLocation.getText());

				JOptionPane.showMessageDialog(this, "Event deleted successfully.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
				refreshUI();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error deleting event: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// 添加 refreshUI 方法
	private void refreshUI() {
		mainPanel.removeAll();
		mainPanel.add(createBrowsePanel(), "Browse");
		mainPanel.add(createManagePanel(), "Manage");
		mainPanel.add(createBookingPanel(), "Book");
		mainPanel.add(createCancelPanel(), "Cancel");
		cardLayout.show(mainPanel, "Manage");
	}

	private JPanel loadAndDisplayEvents(boolean bookingEnabled, boolean cancelEnabled, boolean manageEnabled) {
		JPanel eventListPanel = new JPanel(new GridLayout(0, 3, 10, 10));
		eventListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		List<Event> events = loadEventsFromFile(Organizer.DEFAULT_FILE_PATH);
		for (Event event : events) {
			JPanel eventPanel = new JPanel(new BorderLayout());
			eventPanel.setBorder(BorderFactory.createTitledBorder("Event: " + event.getEventName()));

			JTextArea eventInfo = new JTextArea();
			eventInfo.setEditable(false);
			eventInfo.setFont(new Font("Monospaced", Font.PLAIN, 14));
			eventInfo.setText(getEventInfoText(event));
			eventPanel.add(new JScrollPane(eventInfo), BorderLayout.CENTER);

			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			if (bookingEnabled) {
				JButton btnRegular = new JButton("Regular Book");
				JButton btnVIP = new JButton("VIP Book");

				btnRegular.addActionListener(e -> handleTicketBooking(event, false, eventInfo));
				btnVIP.addActionListener(e -> handleTicketBooking(event, true, eventInfo));

				buttonPanel.add(btnRegular);
				buttonPanel.add(btnVIP);
			}

			if (cancelEnabled) {
				JButton btnCancel = new JButton("Cancel Booking");
				btnCancel.addActionListener(e -> handleTicketCancellation(event, eventListPanel, eventPanel));
				buttonPanel.add(btnCancel);
			}

			eventPanel.add(buttonPanel, BorderLayout.SOUTH);
			eventListPanel.add(eventPanel);
		}
		return eventListPanel;
	}

	private void handleTicketBooking(Event event, boolean isVip, JTextArea eventInfo) {
		if (!(currentUser instanceof Consumer)) {
			JOptionPane.showMessageDialog(this, "Current user does not have booking permissions.", "Permission Denied",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String input = JOptionPane.showInputDialog(this, "Enter the number of tickets:");
		try {
			int amount = Integer.parseInt(input);
			Consumer consumer = (Consumer) currentUser;

			if (isVip) {
				if (consumer instanceof Vip) {
					((Vip) consumer).vipBooking(amount, event);
					JOptionPane.showMessageDialog(this, "VIP Tickets booked successfully.", "Success",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, "Only VIP users can book VIP tickets.", "Permission Denied",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {
				consumer.bookTicket(amount, event);
				JOptionPane.showMessageDialog(this, "Regular Tickets booked successfully.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			}

			// 更新 eventInfo.txt 文件
			updateEventFile(event);

			// 刷新事件信息
			eventInfo.setText(getEventInfoText(event));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Invalid number entered.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (UnauthorizedAccessException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Permission Denied", JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleTicketCancellation(Event event, JPanel eventListPanel, JPanel eventPanel) {
		if (!(currentUser instanceof Consumer)) {
			JOptionPane.showMessageDialog(this, "Current user does not have cancellation permissions.",
					"Permission Denied", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String input = JOptionPane.showInputDialog(this, "Enter the number of tickets to cancel:");
		try {
			int amount = Integer.parseInt(input);
			Consumer consumer = (Consumer) currentUser;
			consumer.cancelTicket(amount, event);

			// 更新 eventInfo.txt 文件
			updateEventFile(event);

			// 刷新界面
			eventListPanel.remove(eventPanel);
			eventListPanel.revalidate();
			eventListPanel.repaint();

			JOptionPane.showMessageDialog(this, "Tickets canceled successfully.", "Success",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Invalid number entered.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 新增方法：更新事件文件
	private void updateEventFile(Event event) {
		// 从文件加载所有事件
		List<Event> events = loadEventsFromFile(Organizer.DEFAULT_FILE_PATH);

		// 找到并更新对应的事件
		for (Event e : events) {
			if (e.getEventName().equals(event.getEventName()) && e.getEventDate().equals(event.getEventDate())
					&& e.getEventLocation().equals(event.getEventLocation())) {
				// 更新票数
				e.setEventAmount(event.getEventAmount());
				e.setEventVip(event.getEventVip());
				break;
			}
		}

		// 重新写入文件
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(Organizer.DEFAULT_FILE_PATH))) {
			for (Event e : events) {
				writer.write("Event Info: " + e.getEventName() + ", " + e.getEventDate() + ", " + e.getEventLocation());
				writer.newLine();
				writer.write("VIP Ticket: " + e.getEventVip());
				writer.newLine();
				writer.write("Regular Ticket: " + e.getEventAmount());
				writer.newLine();
				writer.write("Artist: " + e.getArtist());
				writer.newLine();
				writer.newLine();
			}
		} catch (IOException e) {
			System.err.println("Error writing to file: " + e.getMessage());
		}
	}

	private String getEventInfoText(Event event) {
		return "Event Name: " + event.getEventName() + "\n" + "Artist: " + event.getArtist() + "\n" + "Date: "
				+ event.getEventDate() + "\n" + "Location: " + event.getEventLocation() + "\n" + "Regular Tickets: "
				+ event.getEventAmount() + "\n" + "VIP Tickets: " + event.getEventVip();
	}

	private List<Event> loadEventsFromFile(String filePath) {
		List<Event> events = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("Event Info:")) {
					String[] infoParts = line.substring(12).split(", ");
					String eventName = infoParts[0];
					String eventDate = infoParts[1];
					String eventLocation = infoParts[2];

					int vipTickets = Integer.parseInt(reader.readLine().split(": ")[1]);
					int regularTickets = Integer.parseInt(reader.readLine().split(": ")[1]);
					String artist = reader.readLine().split(": ")[1];

					events.add(new Event(eventName, artist, eventDate, regularTickets, vipTickets, eventLocation));
					reader.readLine();
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error loading events: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return events;
	}

	public void setCurrentUser(User user) {
		this.currentUser = user;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			EventManagementSystem frame = new EventManagementSystem();
			frame.setVisible(true);
		});
	}
}