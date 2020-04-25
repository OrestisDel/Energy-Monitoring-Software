using System;
using System.Windows.Forms;

namespace TCP_Client
{
    public partial class ClientLogin : Form
    {
        public ClientLogin()
        {
            InitializeComponent();
        }

        private void btn_Submit_Click(object sender, EventArgs e)
        {
            if (txt_UserName.Text == "" || txt_Password.Text == "")
            {
                MessageBox.Show("Please provide UserName and Password", Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }
            if (Control.IsKeyLocked(Keys.CapsLock))
            {
                MessageBox.Show("The Caps Lock key is ON.", Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }
            try
            {
                //Checks string values
				        //if username equals user and password equals user show client main form 
                if (txt_UserName.Text == "user" & txt_Password.Text == "user")
                {
                    MessageBox.Show("Login Successful!", Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
                    this.Hide();
                    Client cl = new Client();
                    cl.Show();
                }
                else
                {
                    MessageBox.Show("Login Failed!", Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private void txt_Password_TextChanged(object sender, EventArgs e)
        {
		      	//masks characters entered in password text box
            txt_Password.PasswordChar = '*';
        }
    }
}
