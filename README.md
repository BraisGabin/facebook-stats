# Facebook Stats

Create a csv with the statistics of a group, page or the user's timeline.

### How to get it
1. [Download the binary][binary] and unzip it.
2. Build the binary yourself:
```sh
git clone https://github.com/BraisGabin/facebook-stats.git
cd facebook-stats
gradle distZip
```
The binary is generated at `build/distributions/`

### How to use it
```sh
facebook-stats <output_file_path> <fb_user_id_group_id_or_page_id> <fb_access_token> [max_posts]
```

Where:
- `output_file_path` is the name that you want for the csv file generated.
- `fb_user_id_group_id_or_page_id` is the id of the user, group or page that you want to get the stats. It's allways a *long* number. [How can I know that id?](#how-to-know-a-facebook-id).
- `fb_access_token` is the Facebook access token that the script is going to use to get access to the information. [How can I get that?](#how-to-get-a-facebook-access-token).
 - If you want the stats for a user the access token must have the `read_stream` permission.
 - If you want the stats for a close or private group the access token must have the `user_groups` permission.

#### How to know a Facebook id
There is not a cannonical way to find a Facebook id. I'm going to list 3 different ways from easier to harder.

1. Go to the user, group or page timeline and look at the url in your browser. If it's the way: `https://www.facebook.com/19292868552` the number `19292868552` is the id that you are looking for.
2. If the url is different you can use the page: [Lookup-ID.com][lookupid]. Paste the url in that web and it'll show you the id that you are looking for.
3. If Lookup-ID.com doesn't show you the id you can find it yourself:
 1. At the page click with the left botton of your mouse and select `Show source code`.
 2. It'll appear a page with the HTML of the page. Press <kbd>Ctrl</kbd> + <kbd>F</kbd> (<kbd>cmd</kbd> + <kbd>F</kbd> in Mac OS X) to open the find tool.
 3. Find for `profile_id=`, `group_id=` or `page_id=`. The option depends on the type of id you are looking for.
 4. The id is going to be just after that text.

#### How to get a Facebook access token
There are different ways to get an access token but one of the easiest ones is using the Graph API Explorer:

1. Go to the [Graph API Explorer](api-explorer) page.
2. Click at `Get Token > Get Access`,
3. Click the permissions you want to grant, remember:
 - If you want the stats for a user the access token must have the `read_stream` permission.
 - If you want the stats for a close or private group the access token must have the `user_groups` permission.
4. Click at `Get Access Token`.
5. Copy the Access Token that appears at the top of the page. It's a very long with lettera and numbers.


 [binary]: https://github.com/BraisGabin/facebook-stats/releases/download/1.0.1/facebook-stats-1.0.1.zip
 [lookupid]: https://lookup-id.com/
 [api-explorer]: https://developers.facebook.com/tools/explorer/
