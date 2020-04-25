using System;
using System.Data;
using System.Diagnostics;
using System.Windows.Forms;
using MySql.Data.MySqlClient;

namespace login
{
    public partial class frmlogin : Form
    {
        
        public frmlogin()
        {
            InitializeComponent();
        }

        private void Btn_Submit_Click(object sender, EventArgs e)
        {
            if (txt_UserName.Text == "" || txt_Password.Text == "")
            {
                MessageBox.Show("Please provide UserName and Password",Application.ProductName,MessageBoxButtons.OK,MessageBoxIcon.Warning);
                return;
            }
            if (Control.IsKeyLocked(Keys.CapsLock))
            {
                MessageBox.Show("The Caps Lock key is ON.", Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }
            try
            {
                //Create SqlConnection
                MySqlConnection con = new MySqlConnection("server = 127.0.0.1; user id = *******; password=*******; database=newdatabase;");
		//Select everything from logininfo table that corresponds to the text for username and password
                MySqlCommand cmd = new MySqlCommand("Select * from logininfo where user=@Username and PASSWORD=@Password", con);
                cmd.Parameters.AddWithValue("@Username", txt_UserName.Text);
                cmd.Parameters.AddWithValue("@Password", txt_Password.Text);
                con.Open();
		//initialise a new instance of the database with the MySqlCommand
                MySqlDataAdapter adapt = new MySqlDataAdapter(cmd);
                DataSet ds = new DataSet();
		//refresh rows in the data set
                adapt.Fill(ds);
                con.Close();
		//count the rows returned for the MySqlCommand
                int count = ds.Tables[0].Rows.Count;
                //If count is equal to 1, thn show frmMain form
                if (count == 1)
                {
                    MessageBox.Show("Login Successful!", Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
                    this.Hide();
                    frmmain fm = new frmmain();
                    fm.Show();
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

        private void Txt_Password_TextChanged(object sender, EventArgs e)
        {
	    //mask characters entered in the text box
            txt_Password.PasswordChar = '*';
        }
    }
}
